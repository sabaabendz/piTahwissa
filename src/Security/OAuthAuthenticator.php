<?php

namespace App\Security;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use KnpU\OAuth2ClientBundle\Security\Authenticator\OAuth2Authenticator;
use League\OAuth2\Client\Provider\GoogleUser;
use League\OAuth2\Client\Provider\LinkedInResourceOwner;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;
use Symfony\Component\Security\Http\EntryPoint\AuthenticationEntryPointInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Service\EnterpriseCodeGenerator;

class OAuthAuthenticator extends OAuth2Authenticator implements AuthenticationEntryPointInterface
{
    public function __construct(
        private readonly ClientRegistry $clientRegistry,
        private readonly EntityManagerInterface $entityManager,
        private readonly RouterInterface $router,
        private readonly EnterpriseCodeGenerator $enterpriseCodeGenerator,
        private readonly ValidatorInterface $validator
    ) {
    }

    public function supports(Request $request): ?bool
    {
        return in_array($request->attributes->get('_route'), ['connect_google_check', 'connect_linkedin_check']);
    }

    public function authenticate(Request $request): Passport
    {
        $clientKey = $request->attributes->get('_route') === 'connect_google_check' ? 'google' : 'linkedin';
        $client = $this->clientRegistry->getClient($clientKey);
        $accessToken = $this->fetchAccessToken($client);

        return new SelfValidatingPassport(
            new UserBadge($accessToken->getToken(), function () use ($accessToken, $client, $request, $clientKey): User {
                $oauthUser = $client->fetchUserFromToken($accessToken);

                $email = null;
                $externalId = $oauthUser->getId();
                $name = null;

                if ($oauthUser instanceof GoogleUser) {
                    $email = $oauthUser->getEmail();
                    $name = $oauthUser->getName();
                } elseif ($oauthUser instanceof LinkedInResourceOwner) {
                    $email = $oauthUser->getEmail();
                    // LinkedInResourceOwner has getFirstName() and getLastName()
                    $name = $oauthUser->getFirstName() . ' ' . $oauthUser->getLastName();
                }

                if (!$email) {
                    throw new AuthenticationException('Email not provided by ' . ucfirst($clientKey));
                }

                // 1) Find existing user by specific OAuth ID
                $existingUser = $this->entityManager->getRepository(User::class)
                    ->findOneBy([$clientKey . 'Id' => $externalId]);

                if ($existingUser) {
                    return $existingUser;
                }

                // 2) Find existing user by email
                $existingUser = $this->entityManager->getRepository(User::class)
                    ->findOneBy(['email' => $email]);

                if ($existingUser) {
                    // Update the ID for this provider
                    $setter = 'set' . ucfirst($clientKey) . 'Id';
                    $existingUser->$setter($externalId);
                    $this->entityManager->persist($existingUser);
                    $this->entityManager->flush();

                    return $existingUser;
                }

                // 3) Create new user (registration via OAuth)
                $session = $request->getSession();
                $role = $session->get('_oauth_auth_role');
                $session->remove('_oauth_auth_role');

                if ($role === 'manager') {
                    $user = new \App\Entity\Manager();
                    $user->setLevel('Owner');
                    $user->setDepartment('General');
                    $user->setEnterpriseCode($this->enterpriseCodeGenerator->generate());
                } else {
                    $user = new \App\Entity\Collaborator();
                    $user->setPost('New Member');
                    $user->setTeam('General');
                    $user->setEnterpriseCode('P_' . uniqid());
                }

                $user->setEmail($email);
                $user->setName($name ?? $email);
                $setter = 'set' . ucfirst($clientKey) . 'Id';
                $user->$setter($externalId);
                $user->setPassword(null);

                // Validate user entity
                $errors = $this->validator->validate($user);
                if (count($errors) > 0) {
                    $errorMessages = [];
                    foreach ($errors as $error) {
                        $errorMessages[] = $error->getMessage();
                    }
                    throw new AuthenticationException('Validation error: ' . implode(', ', $errorMessages));
                }

                $this->entityManager->persist($user);
                try {
                    $this->entityManager->flush();
                } catch (\Exception $e) {
                    throw new AuthenticationException('Could not create user: ' . $e->getMessage());
                }

                return $user;
            })
        );
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        return new RedirectResponse($this->router->generate('app_dashboard_index'));
    }

    public function onAuthenticationFailure(Request $request, AuthenticationException $exception): ?Response
    {
        $this->saveAuthenticationErrorToSession($request, $exception);
        return new RedirectResponse($this->router->generate('app_login'));
    }

    public function start(Request $request, ?AuthenticationException $authException = null): Response
    {
        return new RedirectResponse($this->router->generate('app_login'));
    }
}

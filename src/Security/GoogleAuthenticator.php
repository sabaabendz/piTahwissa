<?php

namespace App\Security;

use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use KnpU\OAuth2ClientBundle\Security\Authenticator\OAuth2Authenticator;
use League\OAuth2\Client\Provider\GoogleUser;
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

class GoogleAuthenticator extends OAuth2Authenticator implements AuthenticationEntryPointInterface
{
    public function __construct(
        private readonly ClientRegistry $clientRegistry,
        private readonly EntityManagerInterface $entityManager,
        private readonly RouterInterface $router,
        private readonly \App\Service\EnterpriseCodeGenerator $enterpriseCodeGenerator
    ) {
    }

    public function supports(Request $request): ?bool
    {
        return $request->attributes->get('_route') === 'connect_google_check';
    }

    public function authenticate(Request $request): Passport
    {
        $client = $this->clientRegistry->getClient('google');
        $accessToken = $this->fetchAccessToken($client);

        return new SelfValidatingPassport(
            new UserBadge($accessToken->getToken(), function () use ($accessToken, $client, $request): User {
                /** @var GoogleUser $googleUser */
                $googleUser = $client->fetchUserFromToken($accessToken);

                $email = $googleUser->getEmail();
                $googleId = $googleUser->getId();

                // 1) Find existing user by Google ID
                $existingUser = $this->entityManager->getRepository(User::class)
                    ->findOneBy(['googleId' => $googleId]);

                if ($existingUser) {
                    return $existingUser;
                }

                // 2) Find existing user by email
                $existingUser = $this->entityManager->getRepository(User::class)
                    ->findOneBy(['email' => $email]);

                if ($existingUser) {
                    $existingUser->setGoogleId($googleId);
                    $this->entityManager->persist($existingUser);
                    $this->entityManager->flush();

                    return $existingUser;
                }

                // 3) Create new user (registration via Google)
                // Check for role in session
                $session = $request->getSession();
                $role = $session->get('_google_auth_role');
                // Clear the role from session
                $session->remove('_google_auth_role');

                if ($role === 'manager') {
                    $user = new \App\Entity\Manager();
                    // Role is hardcoded in Manager::getRoles()
                    $user->setLevel('Owner'); // Default level
                    $user->setDepartment('General'); // Default department
                    $user->setEnterpriseCode($this->enterpriseCodeGenerator->generate());
                } elseif ($role === 'collaborator') {
                    $user = new \App\Entity\Collaborator();
                    // Role is hardcoded in Collaborator::getRoles()
                    $user->setPost('New Member'); // Default post
                    $user->setTeam('General'); // Default team
                    // Collaborator needs an enterprise code to join.
                    // For now, we set a placeholder as they haven't entered one yet.
                    // They will need to update this in their profile.
                    // NOTE: 'P_' + uniqid() is 2+13 = 15 chars (fits in VARCHAR(20))
                    $user->setEnterpriseCode('P_' . uniqid());
                } else {
                    // Fallback to basic user if no role selected (should be prevented by frontend)
                    // But to be safe, we default to Collaborator structure as it's safer than Manager
                    $user = new \App\Entity\Collaborator();
                    // Role is hardcoded in Collaborator::getRoles()
                    $user->setPost('New Member');
                    $user->setTeam('General');
                    $user->setEnterpriseCode('P_' . uniqid());
                }

                $user->setEmail($email);
                $user->setName($googleUser->getName() ?? $email);
                $user->setGoogleId($googleId);
                $user->setPassword(null);

                $this->entityManager->persist($user);
                try {
                    $this->entityManager->flush();
                } catch (\Exception $e) {
                    // Log error and rethrow authenticaton exception
                    throw new AuthenticationException('Could not create user: ' . $e->getMessage());
                }

                return $user;
            })
        );
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        $targetUrl = $this->router->generate('app_dashboard_index');

        return new RedirectResponse($targetUrl);
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

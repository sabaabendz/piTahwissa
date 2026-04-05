<?php

namespace App\Security;

use App\Entity\Role;
use App\Entity\User;
use Doctrine\ORM\EntityManagerInterface;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use KnpU\OAuth2ClientBundle\Security\Authenticator\OAuth2Authenticator;
use League\OAuth2\Client\Provider\GithubResourceOwner;
use League\OAuth2\Client\Provider\GoogleUser;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\RouterInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\AuthenticationException;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAuthenticationException;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\Authenticator\Passport\SelfValidatingPassport;
use Symfony\Component\Security\Http\EntryPoint\AuthenticationEntryPointInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;

class OAuthAuthenticator extends OAuth2Authenticator implements AuthenticationEntryPointInterface
{
    public function __construct(
        private readonly ClientRegistry $clientRegistry,
        private readonly EntityManagerInterface $entityManager,
        private readonly RouterInterface $router,
        private readonly ValidatorInterface $validator
    ) {
    }

    public function supports(Request $request): ?bool
    {
        $isOAuthCallbackRoute = in_array($request->attributes->get('_route'), ['connect_google_check', 'connect_github_check'], true);

        if (!$isOAuthCallbackRoute) {
            return false;
        }

        return $request->query->has('state') && ($request->query->has('code') || $request->query->has('error'));
    }

    public function authenticate(Request $request): Passport
    {
        if ($request->query->has('error')) {
            $error = (string) $request->query->get('error', 'authorization_error');
            $description = (string) $request->query->get('error_description', 'OAuth authorization failed.');

            throw new CustomUserMessageAuthenticationException(sprintf('OAuth error: %s (%s)', $error, $description));
        }

        $clientKey = $request->attributes->get('_route') === 'connect_github_check' ? 'github' : 'google';
        $client = $this->clientRegistry->getClient($clientKey);
        $accessToken = $this->fetchAccessToken($client);

        return new SelfValidatingPassport(
            new UserBadge($accessToken->getToken(), function () use ($accessToken, $client, $clientKey): User {
                $oauthUser = $client->fetchUserFromToken($accessToken);

                $email = null;
                $externalId = $oauthUser->getId();
                $name = null;

                if ($oauthUser instanceof GoogleUser) {
                    $email = $oauthUser->getEmail();
                    $name = $oauthUser->getName();
                } elseif ($oauthUser instanceof GithubResourceOwner) {
                    $email = $oauthUser->getEmail();
                    $name = $oauthUser->getName() ?: $oauthUser->getNickname();
                }

                if (!$email) {
                    throw new AuthenticationException('Email not provided by ' . ucfirst($clientKey));
                }

                // 1) Find existing user by email
                $existingUser = $this->entityManager->getRepository(User::class)
                    ->findOneBy(['email' => $email]);

                if ($existingUser) {
                    return $existingUser;
                }

                // 2) Create new user with USER role
                $role = $this->entityManager->getRepository(Role::class)->find(1);
                if (!$role) {
                    throw new AuthenticationException('Default role USER (id=1) not found.');
                }

                $user = new User();
                $user->setEmail($email);
                $user->setName((string) ($name ?? $email));
                $user->setIsActive(true);
                $user->setIsVerified(true);
                $user->setRole($role);
                $user->setPassword(password_hash(bin2hex(random_bytes(16)), PASSWORD_BCRYPT));

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

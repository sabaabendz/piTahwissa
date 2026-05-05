<?php

namespace App\Security;

use App\Service\RecaptchaVerifier;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Generator\UrlGeneratorInterface;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAuthenticationException;
use Symfony\Component\Security\Http\Authenticator\AbstractLoginFormAuthenticator;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\CsrfTokenBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\RememberMeBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Badge\UserBadge;
use Symfony\Component\Security\Http\Authenticator\Passport\Credentials\PasswordCredentials;
use Symfony\Component\Security\Http\Authenticator\Passport\Passport;
use Symfony\Component\Security\Http\SecurityRequestAttributes;
use Symfony\Component\Security\Http\Util\TargetPathTrait;

class LoginFormAuthenticator extends AbstractLoginFormAuthenticator
{
    use TargetPathTrait;

    public const LOGIN_ROUTE = 'app_login';

    public function __construct(
        private UrlGeneratorInterface $urlGenerator,
        private RecaptchaVerifier $recaptchaVerifier,
    ) {
    }

    public function authenticate(Request $request): Passport
    {
        $email = (string) $request->request->get('email', '');
        $request->getSession()->set(SecurityRequestAttributes::LAST_USERNAME, $email);

        // ── Human verification gate ─────────────────────────────────────────
        if (!$request->getSession()->get('human_verified', false)) {
            throw new CustomUserMessageAuthenticationException(
                'Veuillez d\'abord vérifier que vous êtes humain'
            );
        }

        // Only verify reCAPTCHA if it's configured (secret key is set)
        if ($this->recaptchaVerifier->isEnabled()) {
            $captchaResponse = (string) ($request->request->get('g-recaptcha-response') ?: $request->request->get('captcha', ''));
            if (!$this->recaptchaVerifier->verify($captchaResponse, $request->getClientIp())) {
                throw new CustomUserMessageAuthenticationException('Veuillez vérifier que vous n\'êtes pas un robot.');
            }
        }

        $csrfToken = $request->request->get('_csrf_token');
        $csrfTokenValue = is_string($csrfToken) ? $csrfToken : null;

        return new Passport(
            new UserBadge($email),
            new PasswordCredentials((string) $request->request->get('password', '')),
            [
                new CsrfTokenBadge('authenticate', $csrfTokenValue),
                new RememberMeBadge(),
            ]
        );
    }

    public function onAuthenticationSuccess(Request $request, TokenInterface $token, string $firewallName): ?Response
    {
        /** @var \App\Entity\User $user */
        $user = $token->getUser();
        $session = $request->getSession();

        // Clear the human verification flag so it must be re-done next login
        $session->remove('human_verified');

        // Bridge: populate session variables used by existing controllers
        $session->set('user_id', $user->getId());
        $session->set('user_name', $user->getName());

        $roleName = strtoupper((string) ($user->getRole()?->getName() ?? 'USER'));
        $sessionRole = match ($roleName) {
            'ADMIN' => 'ADMIN',
            'AGENT' => 'AGENT',
            default => 'CLIENT',
        };
        $session->set('user_role', $sessionRole);

        // Redirect to saved target path if available
        if ($targetPath = $this->getTargetPath($request->getSession(), $firewallName)) {
            return new RedirectResponse($targetPath);
        }

        // Redirect based on role
        return new RedirectResponse(match ($sessionRole) {
            'ADMIN' => $this->urlGenerator->generate('admin_reservation_index'),
            'AGENT' => $this->urlGenerator->generate('agent_reservation_index'),
            'CLIENT' => $this->urlGenerator->generate('client_voyage_index'),
        });
    }

    protected function getLoginUrl(Request $request): string
    {
        return $this->urlGenerator->generate(self::LOGIN_ROUTE);
    }
}

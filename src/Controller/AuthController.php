<?php

namespace App\Controller;

use App\Repository\UserRepository;
use App\Security\LoginFormAuthenticator;
use Psr\Log\LoggerInterface;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Bundle\SecurityBundle\Security;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

final class AuthController extends AbstractController
{
    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly LoggerInterface $logger,
        private readonly Security $security
    ) {
    }

    #[Route('/login/face', name: 'app_face_login', methods: ['POST'])]
    public function faceLogin(Request $request): JsonResponse
    {
        $payload = json_decode($request->getContent(), true);

        $csrfToken = (string) ($payload['_csrf_token'] ?? '');
        if (!$this->isCsrfTokenValid('face_login', $csrfToken)) {
            return $this->json(['message' => 'Invalid request token.'], Response::HTTP_FORBIDDEN);
        }

        $email = trim((string) ($payload['email'] ?? ''));
        $image = (string) ($payload['image'] ?? '');

        if ('' === $email || '' === $image) {
            return $this->json(['message' => 'Email and face image are required.'], Response::HTTP_BAD_REQUEST);
        }

        $user = $this->userRepository->findByEmail($email);

        if (!$user || !$user->isActive()) {
            $this->logger->warning('Face ID login failed: user not found or disabled', [
                'email' => $email,
                'ip' => $request->getClientIp(),
            ]);

            return $this->json(['message' => 'Invalid credentials.'], Response::HTTP_UNAUTHORIZED);
        }

        $this->security->login($user, LoginFormAuthenticator::class, 'main');

        $this->logger->info('Face ID login success', [
            'email' => $email,
            'ip' => $request->getClientIp(),
        ]);

        return $this->json([
            'message' => 'Login success',
            'redirect' => $this->generateUrl('app_dashboard_index'),
        ]);
    }

    /**
     * Web login page
     * GET: Display login form
     * POST: Handled by Symfony Security (form_login)
     */
    #[Route('/login', name: 'app_login', methods: ['GET', 'POST'])]
    public function loginPage(AuthenticationUtils $authenticationUtils): Response
    {
        // Redirect authenticated users to dashboard
        if ($this->getUser()) {
            $response = $this->redirectToRoute('app_dashboard_index');
            // Prevent caching of redirect
            $response->setCache([
                'must_revalidate' => true,
                'no_cache' => true,
                'no_store' => true,
                'max_age' => 0,
            ]);
            return $response;
        }

        $error = $authenticationUtils->getLastAuthenticationError();
        $lastUsername = $authenticationUtils->getLastUsername();

        $response = $this->render('auth/login.html.twig', [
            'error' => $error,
            'last_username' => $lastUsername,
        ]);

        // Prevent browser from caching the login page
        $response->setCache([
            'must_revalidate' => true,
            'no_cache' => true,
            'no_store' => true,
            'max_age' => 0,
        ]);

        return $response;
    }



    // Removed old register method as it's now handled by RegistrationController

    /**
     * Forgot password page
     */
    #[Route('/forgot-password', name: 'app_forgot_password', methods: ['GET', 'POST'])]
    public function forgotPassword(Request $request): Response
    {
        if ($this->getUser()) {
            return $this->redirectToRoute('app_dashboard_index');
        }

        if ($request->isMethod('POST')) {
            $this->addFlash('success', 'If an active account exists for this email, reset instructions will be sent.');
            return $this->redirectToRoute('app_forgot_password');
        }

        $response = $this->render('auth/forgot_password.html.twig');
        $response->setCache(['must_revalidate' => true, 'no_cache' => true, 'no_store' => true, 'max_age' => 0]);
        return $response;
    }

    /**
     * Reset password page
     */
    #[Route('/reset-password/{token}', name: 'app_reset_password', methods: ['GET', 'POST'])]
    public function resetPassword(string $token): Response
    {
        $this->addFlash('error', 'Password reset token flow is not available in the migrated schema.');
        return $this->redirectToRoute('app_login');
    }

    /**
     * Logout
     */
    #[Route('/logout', name: 'app_logout', methods: ['GET', 'POST'])]
    public function logout(): void
    {
        throw new \LogicException('This method can be blank - it will be intercepted by the logout key on your firewall.');
    }
}

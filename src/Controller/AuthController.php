<?php

namespace App\Controller;

use App\Form\LoginFormType;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Process\Process;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

class AuthController extends AbstractController
{
    #[Route('/login', name: 'app_login')]
    public function login(AuthenticationUtils $authenticationUtils): Response
    {
        if ($this->getUser()) {
            return $this->redirectToRoute('app_home');
        }

        $error        = $authenticationUtils->getLastAuthenticationError();
        $lastUsername = $authenticationUtils->getLastUsername();
        $loginForm    = $this->createForm(LoginFormType::class, ['email' => $lastUsername]);

        return $this->render('auth/login.html.twig', [
            'last_username' => $lastUsername,
            'error'         => $error,
            'loginForm'     => $loginForm->createView(),
        ]);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  FORGOT PASSWORD — Step 1: Enter email, send 6-digit code
    // ──────────────────────────────────────────────────────────────────────────

    #[Route('/forgot-password', name: 'app_forgot_password', methods: ['GET', 'POST'])]
    public function forgotPassword(
        Request         $request,
        UserRepository  $userRepo,
        MailerInterface $mailer
    ): Response {
        if ($request->isMethod('POST')) {
            $email = trim((string) $request->request->get('email', ''));
            $user  = $userRepo->findByEmail($email);

            // Always show the same message to avoid user enumeration
            if ($user !== null) {
                // Generate a 6-digit code
                $code    = str_pad((string) random_int(0, 999999), 6, '0', STR_PAD_LEFT);
                $expires = time() + 600; // 10 minutes

                $session = $request->getSession();
                $session->set('pwd_reset_email',   $email);
                $session->set('pwd_reset_code',    $code);
                $session->set('pwd_reset_expires', $expires);
                $session->set('pwd_reset_verified', false);

                // Send code by email
                try {
                    $htmlBody = $this->renderView('emails/reset_code.html.twig', [
                        'code'      => $code,
                        'userName'  => $user->getName(),
                        'expiresIn' => 10,
                    ]);

                    $emailMessage = (new Email())
                        ->from($_ENV['MAILER_FROM'] ?? 'no-reply@tahwissa.com')
                        ->to($email)
                        ->subject('Tahwissa — Code de réinitialisation de mot de passe')
                        ->html($htmlBody);

                    $mailer->send($emailMessage);
                } catch (\Throwable $e) {
                    // Log but do not expose mailer errors to the user
                    error_log('[ForgotPassword] Mailer error: ' . $e->getMessage());
                }
            }

            // Redirect to code verification step
            return $this->redirectToRoute('app_forgot_password_verify');
        }

        return $this->render('auth/forgot_password.html.twig');
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  FORGOT PASSWORD — Step 2: Verify the 6-digit code
    // ──────────────────────────────────────────────────────────────────────────

    #[Route('/forgot-password/verify', name: 'app_forgot_password_verify', methods: ['GET', 'POST'])]
    public function forgotPasswordVerify(Request $request): Response
    {
        $session = $request->getSession();

        // Guard: must have a pending reset
        if (!$session->get('pwd_reset_email') || !$session->get('pwd_reset_code')) {
            return $this->redirectToRoute('app_forgot_password');
        }

        $error = null;

        if ($request->isMethod('POST')) {
            $submitted = trim((string) $request->request->get('code', ''));
            $stored    = (string) $session->get('pwd_reset_code');
            $expires   = (int)   $session->get('pwd_reset_expires', 0);

            if (time() > $expires) {
                $error = 'Le code a expiré. Veuillez recommencer.';
                // Clean session
                $session->remove('pwd_reset_email');
                $session->remove('pwd_reset_code');
                $session->remove('pwd_reset_expires');
            } elseif (!hash_equals($stored, $submitted)) {
                $error = 'Code incorrect. Veuillez réessayer.';
            } else {
                // Code is correct — mark as verified and go to reset
                $session->set('pwd_reset_verified', true);
                return $this->redirectToRoute('app_forgot_password_reset');
            }
        }

        return $this->render('auth/forgot_password_verify.html.twig', [
            'email' => $session->get('pwd_reset_email'),
            'error' => $error,
        ]);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  FORGOT PASSWORD — Step 3: Set new password
    // ──────────────────────────────────────────────────────────────────────────

    #[Route('/forgot-password/reset', name: 'app_forgot_password_reset', methods: ['GET', 'POST'])]
    public function forgotPasswordReset(
        Request                     $request,
        UserRepository              $userRepo,
        EntityManagerInterface      $em,
        UserPasswordHasherInterface $hasher
    ): Response {
        $session = $request->getSession();

        // Guard: must have passed the code verification step
        if (!$session->get('pwd_reset_verified') || !$session->get('pwd_reset_email')) {
            return $this->redirectToRoute('app_forgot_password');
        }

        $error = null;

        if ($request->isMethod('POST')) {
            $password        = (string) $request->request->get('password', '');
            $passwordConfirm = (string) $request->request->get('password_confirm', '');

            if (strlen($password) < 8) {
                $error = 'Le mot de passe doit contenir au moins 8 caractères.';
            } elseif ($password !== $passwordConfirm) {
                $error = 'Les mots de passe ne correspondent pas.';
            } else {
                $email = (string) $session->get('pwd_reset_email');
                $user  = $userRepo->findByEmail($email);

                if ($user !== null) {
                    $user->setPassword($hasher->hashPassword($user, $password));
                    $em->flush();
                }

                // Clean all reset session keys
                foreach (['pwd_reset_email', 'pwd_reset_code', 'pwd_reset_expires', 'pwd_reset_verified'] as $key) {
                    $session->remove($key);
                }

                $this->addFlash('success', 'Mot de passe mis à jour avec succès ! Connectez-vous.');
                return $this->redirectToRoute('app_login');
            }
        }

        return $this->render('auth/forgot_password_reset.html.twig', [
            'error' => $error,
        ]);
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  HUMAN VERIFICATION (biometric)
    // ──────────────────────────────────────────────────────────────────────────

    #[Route('/verify-human', name: 'app_verify_human', methods: ['POST'])]
    public function verifyHuman(Request $request): JsonResponse
    {
        try {
            $pythonExe  = $_ENV['PYTHON_EXECUTABLE'] ?? getenv('PYTHON_EXECUTABLE') ?: 'python';
            $projectDir = $this->getParameter('kernel.project_dir');
            if (!is_string($projectDir)) {
                return new JsonResponse([
                    'success' => false,
                    'message' => '[DEBUG] kernel.project_dir invalide.',
                ]);
            }
            $scriptPath = $projectDir . DIRECTORY_SEPARATOR . 'human_verification.py';

            if (!file_exists($scriptPath)) {
                return new JsonResponse([
                    'success' => false,
                    'message' => '[DEBUG] Script introuvable. Chemin: ' . $scriptPath,
                ]);
            }

            $checkProcess = new Process([$pythonExe, '--version']);
            $checkProcess->setTimeout(5);
            $checkProcess->run();

            if (!$checkProcess->isSuccessful()) {
                return new JsonResponse([
                    'success' => false,
                    'message' => '[DEBUG] Python inaccessible (' . $pythonExe . '). stderr: '
                        . $checkProcess->getErrorOutput(),
                ]);
            }

            $process = new Process([$pythonExe, $scriptPath, 'webcam', '10']);
            $process->setTimeout(30);
            $process->setEnv([
                'PYTHONIOENCODING' => 'utf-8',
                'PYTHONUTF8'       => '1',
            ]);
            $process->run();

            $output = trim($process->getOutput());

            if ($output === '') {
                return new JsonResponse([
                    'success' => false,
                    'message' => '[DEBUG] Stdout vide. Python stderr: ' . $process->getErrorOutput(),
                ]);
            }

            $output = mb_convert_encoding($output, 'UTF-8', 'UTF-8');
            $result = json_decode($output, true);

            if (json_last_error() !== JSON_ERROR_NONE || !is_array($result)) {
                return new JsonResponse([
                    'success' => false,
                    'message' => '[DEBUG] JSON invalide (' . json_last_error_msg()
                        . '). stdout: ' . substr($output, 0, 200)
                        . ' | stderr: ' . $process->getErrorOutput(),
                ]);
            }

            if (!empty($result['success'])) {
                $request->getSession()->set('human_verified', true);
            }

            return new JsonResponse($result);

        } catch (\Throwable $e) {
            return new JsonResponse([
                'success' => false,
                'message' => '[DEBUG] Exception PHP: ' . get_class($e)
                    . ' — ' . $e->getMessage()
                    . ' (ligne ' . $e->getLine() . ')',
            ]);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    //  OAUTH & LOGOUT
    // ──────────────────────────────────────────────────────────────────────────

    #[Route('/connect/google', name: 'connect_google_start')]
    public function connectGoogle(ClientRegistry $clientRegistry): Response
    {
        return $clientRegistry->getClient('google_client')->redirect(['email', 'profile'], []);
    }

    #[Route('/connect/google/check', name: 'connect_google_check')]
    public function connectGoogleCheck(): never
    {
        throw new \LogicException('This code should never be reached.');
    }

    #[Route('/connect/github', name: 'connect_github_start')]
    public function connectGithub(ClientRegistry $clientRegistry): Response
    {
        return $clientRegistry->getClient('github_client')->redirect(['user:email'], []);
    }

    #[Route('/connect/github/check', name: 'connect_github_check')]
    public function connectGithubCheck(): never
    {
        throw new \LogicException('This code should never be reached.');
    }

    #[Route('/logout', name: 'app_logout')]
    public function logout(): void
    {
        throw new \LogicException('This method should not be reached.');
    }
}

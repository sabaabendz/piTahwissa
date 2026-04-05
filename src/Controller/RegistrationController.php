<?php

namespace App\Controller;

use App\Entity\User;
use App\Form\RegistrationFormType;
use App\Repository\RoleRepository;
use App\Service\RecaptchaVerifier;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Form\FormError;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpKernel\KernelInterface;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

class RegistrationController extends AbstractController
{
    #[Route('/register', name: 'app_register')]
    public function register(
        Request $request,
        UserPasswordHasherInterface $userPasswordHasher,
        EntityManagerInterface $entityManager,
        RoleRepository $roleRepository,
        RecaptchaVerifier $recaptchaVerifier,
        KernelInterface $kernel,
        LoggerInterface $logger
    ): Response {
        if ($this->getUser()) {
            return $this->redirectToRoute('app_dashboard_index');
        }

        $user = new User();
        $defaultRole = $roleRepository->findOneBy(['name' => 'USER']) ?? $roleRepository->find(1);
        if ($defaultRole !== null) {
            $user->setRole($defaultRole);
        }

        $form = $this->createForm(RegistrationFormType::class, $user);
        $form->handleRequest($request);

        $shouldValidateRecaptcha = !\in_array($kernel->getEnvironment(), ['dev', 'test'], true);

        if ($form->isSubmitted() && $shouldValidateRecaptcha) {
            $captchaResponse = (string) $request->request->get('g-recaptcha-response', '');
            if (!$recaptchaVerifier->verify($captchaResponse, $request->getClientIp())) {
                $form->get('recaptcha')->addError(new FormError('Please verify that you are not a robot.'));
            }
        }

        if ($form->isSubmitted() && $form->isValid()) {
            if ($defaultRole === null) {
                $form->addError(new FormError('Default role USER (id=1) was not found in role table.'));
            } else {
                $plainPassword = (string) $form->get('plainPassword')->getData();

                $user
                    ->setIsActive(true)
                    ->setIsVerified(false)
                    ->setRole($defaultRole)
                    ->setPassword($userPasswordHasher->hashPassword($user, $plainPassword));

                try {
                    $entityManager->persist($user);
                    $entityManager->flush();
                } catch (\Throwable $e) {
                    $logger->error('Registration persist failed.', [
                        'email' => $user->getEmail(),
                        'exception' => $e,
                    ]);

                    $form->addError(new FormError('Registration failed due to a server error. Please try again.'));

                    return $this->render('security/register.html.twig', [
                        'registrationForm' => $form->createView(),
                    ]);
                }

                $this->addFlash('success', 'Account created successfully.');

                return $this->redirectToRoute('app_login');
            }
        }

        return $this->render('security/register.html.twig', [
            'registrationForm' => $form->createView(),
        ]);
    }
}

<?php

namespace App\Controller;

use App\Entity\Collaborator;
use App\Entity\Manager;
use App\Form\RegistrationFormType;
use App\Service\EnterpriseCodeGenerator;
use App\Service\RecaptchaVerifier;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\Form\FormError;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

class RegistrationController extends AbstractController
{
    #[Route('/register', name: 'app_register')]
    public function register(
        Request $request,
        UserPasswordHasherInterface $userPasswordHasher,
        EntityManagerInterface $entityManager,
        EnterpriseCodeGenerator $enterpriseCodeGenerator,
        RecaptchaVerifier $recaptchaVerifier
    ): Response {
        if ($this->getUser()) {
            return $this->redirectToRoute('app_dashboard_index');
        }

        $form = $this->createForm(RegistrationFormType::class);
        $form->handleRequest($request);

        if ($form->isSubmitted()) {
            $captchaResponse = (string) $request->request->get('g-recaptcha-response', '');
            if (!$recaptchaVerifier->verify($captchaResponse, $request->getClientIp())) {
                $form->get('recaptcha')->addError(new FormError('Please verify that you are not a robot.'));
            }
        }

        if ($form->isSubmitted() && $form->isValid()) {
            $role = $form->get('role')->getData();
            $email = $form->get('email')->getData();
            $name = $form->get('name')->getData();
            $plainPassword = $form->get('password')->getData();

            if ($role === 'manager') {
                $user = new Manager();
                $user->setLevel($form->get('level')->getData());
                $user->setDepartment($form->get('department')->getData());
                
                $enterpriseCode = $enterpriseCodeGenerator->generate();
                $user->setEnterpriseCode($enterpriseCode);
                
                $flashMessage = 'Manager created successfully! Enterprise code: ' . $enterpriseCode;
            } else {
                $user = new Collaborator();
                $user->setPost($form->get('post')->getData());
                $user->setTeam($form->get('team')->getData());
                $user->setEnterpriseCode($form->get('enterpriseCode')->getData());
                
                $flashMessage = 'Collaborator created successfully!';
            }

            $user->setEmail($email);
            $user->setName($name);
            $user->setPassword(
                $userPasswordHasher->hashPassword(
                    $user,
                    $plainPassword
                )
            );

            $entityManager->persist($user);
            $entityManager->flush();

            $this->addFlash('success', $flashMessage);

            return $this->redirectToRoute('app_login');
        }

        return $this->render('security/register.html.twig', [
            'registrationForm' => $form->createView(),
        ]);
    }
}

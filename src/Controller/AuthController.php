<?php

namespace App\Controller;

use App\Entity\Manager;
use App\Entity\Collaborator;
use App\Form\ManagerType;
use App\Form\CollaboratorType;
use App\Repository\ManagerRepository;
use App\Repository\UserRepository;
use App\Service\EnterpriseCodeGenerator;
use Doctrine\ORM\EntityManagerInterface;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Authentication\AuthenticationUtils;

final class AuthController extends AbstractController
{
    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly UserPasswordHasherInterface $passwordHasher,

        private readonly EnterpriseCodeGenerator $enterpriseCodeGenerator,
        private readonly EntityManagerInterface $entityManager,
        private readonly ManagerRepository $managerRepository
    ) {
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



    /**
     * Registration page
     * GET: Display registration form
     * POST: Handle form submission based on role (Manager or Collaborator)
     */
    #[Route('/register', name: 'app_register', methods: ['GET', 'POST'])]
    public function register(Request $request): Response
    {
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

        $role = $request->request->get('role');
        $error = null;
        $form = null;

        // Handle POST: Process form based on role
        if ($request->isMethod('POST')) {
            if ($role === 'manager') {
                $manager = new Manager();
                $form = $this->createForm(ManagerType::class, $manager, ['is_edit' => false]);
                $form->handleRequest($request);

                // Check if form was actually submitted with data (not just role selection)
                if ($form->isSubmitted() && $form->isValid()) {
                    // Generate enterprise code
                    $enterpriseCode = $this->enterpriseCodeGenerator->generate();
                    $manager->setEnterpriseCode($enterpriseCode);

                    // Hash password from form
                    $plainPassword = $form->get('password')->getData();
                    $hashedPassword = $this->passwordHasher->hashPassword($manager, $plainPassword);
                    $manager->setPassword($hashedPassword);

                    $this->entityManager->persist($manager);
                    $this->entityManager->flush();

                    $this->addFlash('success', 'Manager created successfully! Enterprise code: ' . $enterpriseCode);
                    return $this->redirectToRoute('app_login');
                }
            } elseif ($role === 'collaborator') {
                $collaborator = new Collaborator();
                $form = $this->createForm(CollaboratorType::class, $collaborator, ['is_edit' => false]);
                $form->handleRequest($request);

                // Check if form was actually submitted with data (not just role selection)
                if ($form->isSubmitted() && $form->isValid()) {
                    // Hash password from form
                    $plainPassword = $form->get('password')->getData();
                    $hashedPassword = $this->passwordHasher->hashPassword($collaborator, $plainPassword);
                    $collaborator->setPassword($hashedPassword);

                    $this->entityManager->persist($collaborator);
                    $this->entityManager->flush();

                    $this->addFlash('success', 'Collaborator created successfully!');
                    return $this->redirectToRoute('app_login');
                }
            } else {
                $error = 'Please select a role (Manager or Collaborator).';
            }
        }

        $response = $this->render('auth/register.html.twig', [
            'form' => $form,
            'error' => $error,
            'selected_role' => $role,
        ]);

        // Prevent browser from caching the register page
        $response->setCache([
            'must_revalidate' => true,
            'no_cache' => true,
            'no_store' => true,
            'max_age' => 0,
        ]);

        return $response;
    }

    /**
     * Forgot password page
     */
    #[Route('/forgot-password', name: 'app_forgot_password', methods: ['GET'])]
    public function forgotPassword(): Response
    {
        $response = $this->render('auth/forgot_password.html.twig');

        // Prevent browser from caching the forgot password page
        $response->setCache([
            'must_revalidate' => true,
            'no_cache' => true,
            'no_store' => true,
            'max_age' => 0,
        ]);

        return $response;
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

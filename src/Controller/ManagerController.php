<?php

namespace App\Controller;

use App\Entity\Manager;
use App\Form\ManagerType;
use App\Repository\ManagerRepository;
use App\Service\EnterpriseCodeGenerator;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;

final class ManagerController extends AbstractController
{
    public function __construct(
        private readonly EnterpriseCodeGenerator $enterpriseCodeGenerator
    ) {
    }

    /**
     * Web route: List all managers
     */
    #[Route('/manager', name: 'app_manager_index', methods: ['GET'])]
    public function index(ManagerRepository $managerRepository): Response
    {
        $managers = $managerRepository->findAll();

        return $this->render('manager/index.html.twig', [
            'managers' => $managers,
        ]);
    }

    /**
     * API route: List all managers (JSON)
     */
    #[Route('/api/manager', name: 'app_manager_api_index', methods: ['GET'])]
    public function indexApi(ManagerRepository $managerRepository): JsonResponse
    {
        $managers = $managerRepository->findAll();
        $data = array_map(function (Manager $manager) {
            return [
                'id' => $manager->getIdUser(),
                'name' => $manager->getName(),
                'email' => $manager->getEmail(),
                'level' => $manager->getLevel(),
                'department' => $manager->getDepartment(),
                'enterprise_code' => $manager->getEnterpriseCode(),
            ];
        }, $managers);

        return new JsonResponse($data);
    }

    /**
     * Web route: Create new manager (form)
     */
    #[Route('/manager/new', name: 'app_manager_new', methods: ['GET', 'POST'])]
    public function new(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $manager = new Manager();
        $form = $this->createForm(ManagerType::class, $manager, ['is_edit' => false]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Generate enterprise code
            $enterpriseCode = $this->enterpriseCodeGenerator->generate();
            $manager->setEnterpriseCode($enterpriseCode);

            // Hash password from form
            $plainPassword = $form->get('password')->getData();
            $hashedPassword = $passwordHasher->hashPassword(
                $manager,
                $plainPassword
            );
            $manager->setPassword($hashedPassword);

            $entityManager->persist($manager);
            $entityManager->flush();

            $this->addFlash('success', 'Manager created successfully!');

            return $this->redirectToRoute('app_manager_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('manager/new.html.twig', [
            'manager' => $manager,
            'form' => $form,
        ]);
    }

    /**
     * API route: Create new manager (JSON)
     */
    #[Route('/api/manager/new', name: 'app_manager_api_new', methods: ['POST'])]
    public function newApi(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        ValidatorInterface $validator
    ): JsonResponse {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(
                ['error' => 'Invalid JSON'],
                Response::HTTP_BAD_REQUEST
            );
        }

        $manager = new Manager();
        $manager->setName($data['name'] ?? '');
        $manager->setEmail($data['email'] ?? '');
        $manager->setPassword($data['password'] ?? '');
        $manager->setLevel($data['level'] ?? '');
        $manager->setDepartment($data['department'] ?? '');

        // Generate enterprise code
        $enterpriseCode = $this->enterpriseCodeGenerator->generate();
        $manager->setEnterpriseCode($enterpriseCode);

        // Validate entity
        $errors = $validator->validate($manager);
        if (count($errors) > 0) {
            $errorMessages = [];
            foreach ($errors as $error) {
                $errorMessages[$error->getPropertyPath()] = $error->getMessage();
            }
            return new JsonResponse(
                ['errors' => $errorMessages],
                Response::HTTP_BAD_REQUEST
            );
        }

        // Check if email already exists
        $existingManager = $entityManager->getRepository(Manager::class)
            ->findOneBy(['email' => $manager->getEmail()]);
        if ($existingManager) {
            return new JsonResponse(
                ['error' => 'Email already exists'],
                Response::HTTP_CONFLICT
            );
        }

            $hashedPassword = $passwordHasher->hashPassword(
                $manager,
                $manager->getPassword()
            );
            $manager->setPassword($hashedPassword);

            $entityManager->persist($manager);
            $entityManager->flush();

        return new JsonResponse([
            'message' => 'Manager created successfully',
            'manager' => [
                'id' => $manager->getIdUser(),
                'name' => $manager->getName(),
                'email' => $manager->getEmail(),
                'level' => $manager->getLevel(),
                'department' => $manager->getDepartment(),
                'enterprise_code' => $manager->getEnterpriseCode(),
            ],
        ], Response::HTTP_CREATED);
    }

    /**
     * Web route: Show manager details
     */
    #[Route('/manager/{idUser}', name: 'app_manager_show', methods: ['GET'])]
    public function show(Manager $manager): Response
    {
        return $this->render('manager/show.html.twig', [
            'manager' => $manager,
        ]);
    }

    /**
     * API route: Show manager details (JSON)
     */
    #[Route('/api/manager/{idUser}', name: 'app_manager_api_show', methods: ['GET'])]
    public function showApi(Manager $manager): JsonResponse
    {
        return new JsonResponse([
            'id' => $manager->getIdUser(),
            'name' => $manager->getName(),
            'email' => $manager->getEmail(),
            'level' => $manager->getLevel(),
            'department' => $manager->getDepartment(),
            'enterprise_code' => $manager->getEnterpriseCode(),
        ]);
    }

    /**
     * Web route: Edit manager
     */
    #[Route('/manager/{idUser}/edit', name: 'app_manager_edit', methods: ['GET', 'POST'])]
    public function edit(
        Request $request,
        Manager $manager,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $form = $this->createForm(ManagerType::class, $manager, ['is_edit' => true]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Only hash password if a new one was provided
            $plainPassword = $form->get('password')->getData();
            if ($plainPassword) {
                $hashedPassword = $passwordHasher->hashPassword(
                    $manager,
                    $plainPassword
                );
                $manager->setPassword($hashedPassword);
            }

            $entityManager->flush();

            $this->addFlash('success', 'Manager updated successfully!');

            return $this->redirectToRoute('app_manager_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('manager/edit.html.twig', [
            'manager' => $manager,
            'form' => $form,
        ]);
    }
}

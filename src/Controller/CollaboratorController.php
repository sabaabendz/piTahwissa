<?php

namespace App\Controller;

use App\Entity\Collaborator;
use App\Entity\Manager;
use App\Form\CollaboratorType;
use App\Repository\CollaboratorRepository;
use App\Repository\ManagerRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;

final class CollaboratorController extends AbstractController
{
    /**
     * Web route: List all collaborators (filtered by enterprise_code for tenant isolation)
     */
    #[Route('/collaborator', name: 'app_collaborator_index', methods: ['GET'])]
    public function index(CollaboratorRepository $collaboratorRepository): Response
    {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to view collaborators.');
        }

        // Get enterprise code based on user type
        $enterpriseCode = null;
        if ($user instanceof Manager) {
            $enterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof Collaborator) {
            $enterpriseCode = $user->getEnterpriseCode();
        }

        if (!$enterpriseCode) {
            throw $this->createAccessDeniedException('No enterprise code found for your account.');
        }

        // Filter collaborators by enterprise code (tenant isolation)
        $collaborators = $collaboratorRepository->findByEnterpriseCode($enterpriseCode);

        return $this->render('collaborator/index.html.twig', [
            'collaborators' => $collaborators,
        ]);
    }

    /**
     * API route: List all collaborators (JSON) - filtered by enterprise_code for tenant isolation
     */
    #[Route('/api/collaborator', name: 'app_collaborator_api_index', methods: ['GET'])]
    public function indexApi(CollaboratorRepository $collaboratorRepository): JsonResponse
    {
        $user = $this->getUser();
        
        if (!$user) {
            return new JsonResponse(
                ['error' => 'Authentication required'],
                Response::HTTP_UNAUTHORIZED
            );
        }

        // Get enterprise code based on user type
        $enterpriseCode = null;
        if ($user instanceof Manager) {
            $enterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof Collaborator) {
            $enterpriseCode = $user->getEnterpriseCode();
        }

        if (!$enterpriseCode) {
            return new JsonResponse(
                ['error' => 'No enterprise code found for your account'],
                Response::HTTP_FORBIDDEN
            );
        }

        // Filter collaborators by enterprise code (tenant isolation)
        $collaborators = $collaboratorRepository->findByEnterpriseCode($enterpriseCode);
        
        $data = array_map(function (Collaborator $collaborator) {
            return [
                'id' => $collaborator->getIdUser(),
                'name' => $collaborator->getName(),
                'email' => $collaborator->getEmail(),
                'post' => $collaborator->getPost(),
                'team' => $collaborator->getTeam(),
                'enterprise_code' => $collaborator->getEnterpriseCode(),
            ];
        }, $collaborators);

        return new JsonResponse($data);
    }

    /**
     * Web route: Create new collaborator (form)
     */
    #[Route('/collaborator/new', name: 'app_collaborator_new', methods: ['GET', 'POST'])]
    public function new(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        ManagerRepository $managerRepository
    ): Response {
        $collaborator = new Collaborator();
        $form = $this->createForm(CollaboratorType::class, $collaborator, ['is_edit' => false]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Validate enterprise code
            $enterpriseCode = $collaborator->getEnterpriseCode();
            $manager = $managerRepository->findOneBy(['enterpriseCode' => $enterpriseCode]);
            
            if (!$manager) {
                $this->addFlash('error', 'Invalid enterprise code.');
                return $this->render('collaborator/new.html.twig', [
                    'collaborator' => $collaborator,
                    'form' => $form,
                ]);
            }

            // Hash password from form
            $plainPassword = $form->get('password')->getData();
            $hashedPassword = $passwordHasher->hashPassword(
                $collaborator,
                $plainPassword
            );
            $collaborator->setPassword($hashedPassword);

            $entityManager->persist($collaborator);
            $entityManager->flush();

            $this->addFlash('success', 'Collaborator created successfully!');

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/new.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }

    /**
     * API route: Create new collaborator (JSON)
     */
    #[Route('/api/collaborator/new', name: 'app_collaborator_api_new', methods: ['POST'])]
    public function newApi(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        ManagerRepository $managerRepository,
        ValidatorInterface $validator
    ): JsonResponse {
        $data = json_decode($request->getContent(), true);

        if (!$data) {
            return new JsonResponse(
                ['error' => 'Invalid JSON'],
                Response::HTTP_BAD_REQUEST
            );
        }

        // Validate enterprise code
        $enterpriseCode = $data['enterpriseCode'] ?? null;
        if (!$enterpriseCode) {
            return new JsonResponse(
                ['error' => 'Enterprise code is required'],
                Response::HTTP_BAD_REQUEST
            );
        }

        // Check if enterprise code exists
        $manager = $managerRepository->findOneBy(['enterpriseCode' => $enterpriseCode]);
        if (!$manager) {
            return new JsonResponse(
                ['error' => 'Invalid enterprise code'],
                Response::HTTP_BAD_REQUEST
            );
        }

        $collaborator = new Collaborator();
        $collaborator->setName($data['name'] ?? '');
        $collaborator->setEmail($data['email'] ?? '');
        $collaborator->setPassword($data['password'] ?? '');
        $collaborator->setPost($data['post'] ?? '');
        $collaborator->setTeam($data['team'] ?? '');
        $collaborator->setEnterpriseCode($enterpriseCode);

        // Validate entity
        $errors = $validator->validate($collaborator);
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
        $existingCollaborator = $entityManager->getRepository(Collaborator::class)
            ->findOneBy(['email' => $collaborator->getEmail()]);
        if ($existingCollaborator) {
            return new JsonResponse(
                ['error' => 'Email already exists'],
                Response::HTTP_CONFLICT
            );
        }

            $hashedPassword = $passwordHasher->hashPassword(
                $collaborator,
                $collaborator->getPassword()
            );
            $collaborator->setPassword($hashedPassword);

            $entityManager->persist($collaborator);
            $entityManager->flush();

        return new JsonResponse([
            'message' => 'Collaborator created successfully',
            'collaborator' => [
                'id' => $collaborator->getIdUser(),
                'name' => $collaborator->getName(),
                'email' => $collaborator->getEmail(),
                'post' => $collaborator->getPost(),
                'team' => $collaborator->getTeam(),
                'enterprise_code' => $collaborator->getEnterpriseCode(),
            ],
        ], Response::HTTP_CREATED);
    }

    /**
     * Web route: Show collaborator details (with tenant isolation)
     */
    #[Route('/collaborator/{idUser}', name: 'app_collaborator_show', methods: ['GET'])]
    public function show(Collaborator $collaborator): Response
    {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to view collaborator details.');
        }

        // Get enterprise code based on user type
        $userEnterpriseCode = null;
        if ($user instanceof Manager) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof Collaborator) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        }

        // Tenant isolation: Ensure the collaborator belongs to the same enterprise
        if (!$userEnterpriseCode || $collaborator->getEnterpriseCode() !== $userEnterpriseCode) {
            throw $this->createAccessDeniedException('You do not have access to this collaborator.');
        }

        return $this->render('collaborator/show.html.twig', [
            'collaborator' => $collaborator,
        ]);
    }

    /**
     * API route: Show collaborator details (JSON) - with tenant isolation
     */
    #[Route('/api/collaborator/{idUser}', name: 'app_collaborator_api_show', methods: ['GET'])]
    public function showApi(Collaborator $collaborator): JsonResponse
    {
        $user = $this->getUser();
        
        if (!$user) {
            return new JsonResponse(
                ['error' => 'Authentication required'],
                Response::HTTP_UNAUTHORIZED
            );
        }

        // Get enterprise code based on user type
        $userEnterpriseCode = null;
        if ($user instanceof Manager) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof Collaborator) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        }

        // Tenant isolation: Ensure the collaborator belongs to the same enterprise
        if (!$userEnterpriseCode || $collaborator->getEnterpriseCode() !== $userEnterpriseCode) {
            return new JsonResponse(
                ['error' => 'You do not have access to this collaborator'],
                Response::HTTP_FORBIDDEN
            );
        }

        return new JsonResponse([
            'id' => $collaborator->getIdUser(),
            'name' => $collaborator->getName(),
            'email' => $collaborator->getEmail(),
            'post' => $collaborator->getPost(),
            'team' => $collaborator->getTeam(),
            'enterprise_code' => $collaborator->getEnterpriseCode(),
        ]);
    }

    /**
     * Web route: Edit collaborator (with tenant isolation)
     */
    #[Route('/collaborator/{idUser}/edit', name: 'app_collaborator_edit', methods: ['GET', 'POST'])]
    public function edit(
        Request $request,
        Collaborator $collaborator,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to edit collaborators.');
        }

        // Get enterprise code based on user type
        $userEnterpriseCode = null;
        if ($user instanceof Manager) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof Collaborator) {
            $userEnterpriseCode = $user->getEnterpriseCode();
        }

        // Tenant isolation: Ensure the collaborator belongs to the same enterprise
        if (!$userEnterpriseCode || $collaborator->getEnterpriseCode() !== $userEnterpriseCode) {
            throw $this->createAccessDeniedException('You do not have access to edit this collaborator.');
        }

        $form = $this->createForm(CollaboratorType::class, $collaborator, ['is_edit' => true]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Only hash password if a new one was provided
            $plainPassword = $form->get('password')->getData();
            if ($plainPassword) {
                $hashedPassword = $passwordHasher->hashPassword(
                    $collaborator,
                    $plainPassword
                );
                $collaborator->setPassword($hashedPassword);
            }

            $entityManager->flush();

            $this->addFlash('success', 'Collaborator updated successfully!');

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/edit.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }
}

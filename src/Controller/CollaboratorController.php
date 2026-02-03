<?php

namespace App\Controller;

use App\Entity\Collaborator;
use App\Entity\Manager;
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

#[Route('/api/collaborator')]
final class CollaboratorController extends AbstractController
{
    #[Route(name: 'app_collaborator_index', methods: ['GET'])]
    public function index(CollaboratorRepository $collaboratorRepository): JsonResponse
    {
        $collaborators = $collaboratorRepository->findAll();
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

    #[Route('/new', name: 'app_collaborator_new', methods: ['POST'])]
    public function new(
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

    #[Route('/{idUser}', name: 'app_collaborator_show', methods: ['GET'])]
    public function show(Collaborator $collaborator): JsonResponse
    {
        return new JsonResponse([
            'id' => $collaborator->getIdUser(),
            'name' => $collaborator->getName(),
            'email' => $collaborator->getEmail(),
            'post' => $collaborator->getPost(),
            'team' => $collaborator->getTeam(),
            'enterprise_code' => $collaborator->getEnterpriseCode(),
        ]);
    }
}

<?php

namespace App\Controller;

use App\Entity\Manager;
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

#[Route('/api/manager')]
final class ManagerController extends AbstractController
{
    public function __construct(
        private readonly EnterpriseCodeGenerator $enterpriseCodeGenerator
    ) {
    }

    #[Route(name: 'app_manager_index', methods: ['GET'])]
    public function index(ManagerRepository $managerRepository): JsonResponse
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

    #[Route('/new', name: 'app_manager_new', methods: ['POST'])]
    public function new(
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

    #[Route('/{idUser}', name: 'app_manager_show', methods: ['GET'])]
    public function show(Manager $manager): JsonResponse
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
}

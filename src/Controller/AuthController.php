<?php

namespace App\Controller;

use App\Entity\User;
use App\Repository\UserRepository;
use Lexik\Bundle\JWTAuthenticationBundle\Services\JWTTokenManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/api')]
final class AuthController extends AbstractController
{
    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly UserPasswordHasherInterface $passwordHasher,
        private readonly JWTTokenManagerInterface $jwtManager
    ) {
    }

    #[Route('/login', name: 'app_login', methods: ['POST'])]
    public function login(Request $request): JsonResponse
    {
        $data = json_decode($request->getContent(), true);

        if (!isset($data['email']) || !isset($data['password'])) {
            return new JsonResponse(
                ['error' => 'Email and password are required'],
                Response::HTTP_BAD_REQUEST
            );
        }

        $user = $this->userRepository->findOneBy(['email' => $data['email']]);

        if (!$user || !$this->passwordHasher->isPasswordValid($user, $data['password'])) {
            return new JsonResponse(
                ['error' => 'Invalid credentials'],
                Response::HTTP_UNAUTHORIZED
            );
        }

        // Generate JWT token
        $token = $this->jwtManager->create($user);

        // Determine role
        $roles = $user->getRoles();
        $role = in_array('ROLE_MANAGER', $roles) ? 'ROLE_MANAGER' : 'ROLE_COLLABORATOR';

        // Get enterprise code based on user type
        $enterpriseCode = null;
        if ($user instanceof \App\Entity\Manager) {
            $enterpriseCode = $user->getEnterpriseCode();
        } elseif ($user instanceof \App\Entity\Collaborator) {
            $enterpriseCode = $user->getEnterpriseCode();
        }

        return new JsonResponse([
            'token' => $token,
            'role' => $role,
            'enterprise_code' => $enterpriseCode,
            'user' => [
                'id' => $user->getIdUser(),
                'email' => $user->getEmail(),
                'name' => $user->getName(),
            ],
        ]);
    }
}

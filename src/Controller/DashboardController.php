<?php

namespace App\Controller;

use App\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class DashboardController extends AbstractController
{
    #[Route('/dashboard', name: 'app_dashboard_index', methods: ['GET'])]
    #[Route('/dashboard', name: 'app_dashboard', methods: ['GET'])]
    public function index(
        UserRepository $userRepository
    ): Response {
        $agentsCount = count($userRepository->findByRole('AGENT'));
        $usersCount = count($userRepository->findByRole('USER'));

        $response = $this->render('dashboard/index.html.twig', [
            'agents_count' => $agentsCount,
            'users_count' => $usersCount,
            'meetings_count' => 0, // Placeholder
        ]);

        // Prevent browser from caching protected pages
        $response->setCache([
            'must_revalidate' => true,
            'no_cache' => true,
            'no_store' => true,
            'max_age' => 0,
        ]);

        return $response;
    }
}

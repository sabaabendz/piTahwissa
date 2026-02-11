<?php

namespace App\Controller;

use App\Repository\ManagerRepository;
use App\Repository\CollaboratorRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

final class DashboardController extends AbstractController
{
    #[Route('/dashboard', name: 'app_dashboard_index', methods: ['GET'])]
    public function index(
        ManagerRepository $managerRepository,
        CollaboratorRepository $collaboratorRepository
    ): Response {
        $managersCount = $managerRepository->count([]);
        $collaboratorsCount = $collaboratorRepository->count([]);

        $response = $this->render('dashboard/index.html.twig', [
            'managers_count' => $managersCount,
            'collaborators_count' => $collaboratorsCount,
            'tasks_count' => 0, // Placeholder until Task entity is created
            'meetings_count' => 0, // Placeholder until Meeting entity is created
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

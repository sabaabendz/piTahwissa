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
        CollaboratorRepository $collaboratorRepository,
        \App\Repository\ProjetRepository $projetRepository,
        \App\Repository\TacheRepository $tacheRepository
    ): Response {
        $managersCount = $managerRepository->count([]);
        $collaboratorsCount = $collaboratorRepository->count([]);
        $projectsCount = $projetRepository->count([]);
        $tasksCount = $tacheRepository->count([]);

        $response = $this->render('dashboard/index.html.twig', [
            'managers_count' => $managersCount,
            'collaborators_count' => $collaboratorsCount,
            'projects_count' => $projectsCount,
            'tasks_count' => $tasksCount,
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

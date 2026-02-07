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

        return $this->render('dashboard/index.html.twig', [
            'managers_count' => $managersCount,
            'collaborators_count' => $collaboratorsCount,
            'tasks_count' => 0, // Placeholder until Task entity is created
            'meetings_count' => 0, // Placeholder until Meeting entity is created
        ]);
    }
}

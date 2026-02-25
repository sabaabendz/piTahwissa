<?php

namespace App\Controller\Admin;

use App\Repository\ProjetRepository;
use App\Repository\TacheRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/admin')]
class DashboardController extends AbstractController
{
    #[Route('/', name: 'app_admin_dashboard', methods: ['GET'])]
    public function index(ProjetRepository $projetRepository, TacheRepository $tacheRepository): Response
    {
        $projets = $projetRepository->findBy([], ['id' => 'DESC'], 5);
        $taches = $tacheRepository->findBy([], ['id' => 'DESC'], 5);
        $totalProjets = $projetRepository->count([]);
        $totalTaches = $tacheRepository->count([]);
        
        // Statistiques des projets par statut
        $statutStats = [
            'en_attente' => $projetRepository->count(['statut' => 'en_attente']),
            'actif' => $projetRepository->count(['statut' => 'actif']),
            'termine' => $projetRepository->count(['statut' => 'termine']),
        ];
        
        return $this->render('admin/dashboard.html.twig', [
            'projets' => $projets,
            'taches' => $taches,
            'totalProjets' => $totalProjets,
            'totalTaches' => $totalTaches,
            'statutStats' => $statutStats,
        ]);
    }
}

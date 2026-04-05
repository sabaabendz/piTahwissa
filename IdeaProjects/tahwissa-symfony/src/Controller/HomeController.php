<?php

namespace App\Controller;

use App\Repository\ReservationVoyageRepository;
use App\Repository\VoyageRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home')]
    public function index(VoyageRepository $voyageRepository, ReservationVoyageRepository $reservationRepository): Response
    {
        $voyages = $voyageRepository->findActifs();
        $stats = $reservationRepository->getStats();
        $voyageStats = $voyageRepository->getStats();

        return $this->render('home/index.html.twig', [
            'voyages' => $voyages,
            'stats' => $stats,
            'voyageStats' => $voyageStats,
        ]);
    }

    /**
     * Role switcher - simulates login for different roles
     * Since authentication is handled by another team member,
     * we use session to store the current role
     */
    #[Route('/switch-role/{role}', name: 'app_switch_role')]
    public function switchRole(Request $request, string $role): Response
    {
        $validRoles = ['ADMIN', 'AGENT', 'CLIENT'];
        if (!in_array(strtoupper($role), $validRoles)) {
            $this->addFlash('danger', 'Rôle invalide.');
            return $this->redirectToRoute('app_home');
        }

        $session = $request->getSession();
        $role = strtoupper($role);
        $session->set('user_role', $role);

        // Simulate user IDs for each role
        $userIds = ['ADMIN' => 1, 'AGENT' => 2, 'CLIENT' => 6];
        $userNames = ['ADMIN' => 'Administrateur', 'AGENT' => 'Agent Voyage', 'CLIENT' => 'Sabaa Bendziri'];
        $session->set('user_id', $userIds[$role]);
        $session->set('user_name', $userNames[$role]);

        $this->addFlash('success', 'Vous êtes maintenant connecté en tant que ' . $userNames[$role] . ' (' . $role . ')');

        return match ($role) {
            'ADMIN' => $this->redirectToRoute('admin_reservation_index'),
            'AGENT' => $this->redirectToRoute('agent_reservation_index'),
            'CLIENT' => $this->redirectToRoute('client_voyage_index'),
            default => $this->redirectToRoute('app_home'),
        };
    }
}

<?php

namespace App\Controller;

use App\Entity\ReservationVoyage;
use App\Form\ReservationVoyageType;
use App\Repository\VoyageRepository;
use App\Repository\ReservationVoyageRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

/**
 * CLIENT Controller - Browse voyages and manage own reservations
 */
#[Route('/client')]
class ClientController extends AbstractController
{
    // ===================== VOYAGES (Read only - Catalogue) =====================

    #[Route('/voyage', name: 'client_voyage_index')]
    public function voyageIndex(VoyageRepository $voyageRepository, Request $request): Response
    {
        $this->ensureRole($request);
        $search = $request->query->get('search', '');
        $voyages = $search ? $voyageRepository->search($search) : $voyageRepository->findActifs();

        return $this->render('client/voyage/index.html.twig', [
            'voyages' => $voyages,
            'search' => $search,
        ]);
    }

    #[Route('/voyage/{id}', name: 'client_voyage_show', requirements: ['id' => '\d+'])]
    public function voyageShow(Request $request, \App\Entity\Voyage $voyage): Response
    {
        $this->ensureRole($request);
        return $this->render('client/voyage/show.html.twig', [
            'voyage' => $voyage,
        ]);
    }

    // ===================== RESERVATIONS (Own only) =====================

    #[Route('/reservation', name: 'client_reservation_index')]
    public function reservationIndex(ReservationVoyageRepository $reservationRepository, Request $request): Response
    {
        $this->ensureRole($request);
        $userId = $request->getSession()->get('user_id', 6);
        $reservations = $reservationRepository->findByUser($userId);
        $stats = $reservationRepository->getStats();

        return $this->render('client/reservation/index.html.twig', [
            'reservations' => $reservations,
            'stats' => $stats,
        ]);
    }

    #[Route('/reservation/new/{voyageId}', name: 'client_reservation_new', requirements: ['voyageId' => '\d+'], defaults: ['voyageId' => null])]
    public function reservationNew(Request $request, EntityManagerInterface $em, VoyageRepository $voyageRepository, ?int $voyageId = null): Response
    {
        $this->ensureRole($request);
        $reservation = new ReservationVoyage();
        $userId = $request->getSession()->get('user_id', 6);
        $reservation->setIdUtilisateur($userId);

        // Pre-select voyage if ID is provided
        if ($voyageId) {
            $voyage = $voyageRepository->find($voyageId);
            if ($voyage) {
                $reservation->setVoyage($voyage);
            }
        }

        $form = $this->createForm(ReservationVoyageType::class, $reservation, ['user_role' => 'CLIENT']);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Validate available places
            $voyage = $reservation->getVoyage();
            if ($voyage && $reservation->getNbrPersonnes() > $voyage->getPlacesDisponibles()) {
                $this->addFlash('danger', 'Désolé, il n\'y a que ' . $voyage->getPlacesDisponibles() . ' places disponibles pour ce voyage.');
                return $this->render('client/reservation/new.html.twig', [
                    'form' => $form->createView(),
                ]);
            }

            $reservation->setStatut('EN_ATTENTE');
            $reservation->calculerMontantTotal();
            $em->persist($reservation);
            $em->flush();
            $this->addFlash('success', 'Votre réservation a été créée avec succès ! Montant total: ' . $reservation->getMontantTotal() . ' TND');
            return $this->redirectToRoute('client_reservation_index');
        }

        return $this->render('client/reservation/new.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/reservation/{id}', name: 'client_reservation_show', requirements: ['id' => '\d+'])]
    public function reservationShow(Request $request, ReservationVoyage $reservation): Response
    {
        $this->ensureRole($request);
        $userId = $request->getSession()->get('user_id', 6);
        if ($reservation->getIdUtilisateur() !== $userId) {
            $this->addFlash('danger', 'Vous n\'avez pas accès à cette réservation.');
            return $this->redirectToRoute('client_reservation_index');
        }

        return $this->render('client/reservation/show.html.twig', [
            'reservation' => $reservation,
        ]);
    }

    #[Route('/reservation/{id}/cancel', name: 'client_reservation_cancel', requirements: ['id' => '\d+'], methods: ['POST'])]
    public function reservationCancel(Request $request, ReservationVoyage $reservation, EntityManagerInterface $em): Response
    {
        $this->ensureRole($request);
        $userId = $request->getSession()->get('user_id', 6);
        if ($reservation->getIdUtilisateur() !== $userId) {
            $this->addFlash('danger', 'Vous n\'avez pas accès à cette réservation.');
            return $this->redirectToRoute('client_reservation_index');
        }

        if ($this->isCsrfTokenValid('cancel' . $reservation->getId(), $request->request->get('_token'))) {
            if ($reservation->getStatut() === 'EN_ATTENTE') {
                $reservation->setStatut('ANNULEE');
                $em->flush();
                $this->addFlash('success', 'Votre réservation a été annulée.');
            } else {
                $this->addFlash('warning', 'Seules les réservations en attente peuvent être annulées.');
            }
        }

        return $this->redirectToRoute('client_reservation_index');
    }

    private function ensureRole(Request $request): void
    {
        $role = $request->getSession()->get('user_role');
        if ($role !== 'CLIENT') {
            $request->getSession()->set('user_role', 'CLIENT');
            $request->getSession()->set('user_id', 6);
            $request->getSession()->set('user_name', 'Sabaa Bendziri');
        }
    }
}

<?php

namespace App\Controller;

use App\Entity\Voyage;
use App\Entity\ReservationVoyage;
use App\Form\VoyageType;
use App\Form\ReservationVoyageType;
use App\Repository\VoyageRepository;
use App\Repository\ReservationVoyageRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

/**
 * ADMIN Controller - Full CRUD access for voyages and reservations
 */
#[Route('/admin')]
class AdminController extends AbstractController
{
    // ===================== VOYAGE CRUD =====================

    #[Route('/voyage', name: 'admin_voyage_index')]
    public function voyageIndex(VoyageRepository $voyageRepository, Request $request): Response
    {
        $this->checkRole($request, 'ADMIN');
        $search = $request->query->get('search', '');
        $voyages = $search ? $voyageRepository->search($search) : $voyageRepository->findBy([], ['createdAt' => 'DESC']);
        $stats = $voyageRepository->getStats();

        return $this->render('admin/voyage/index.html.twig', [
            'voyages' => $voyages,
            'stats' => $stats,
            'search' => $search,
        ]);
    }

    #[Route('/voyage/new', name: 'admin_voyage_new')]
    public function voyageNew(Request $request, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        $voyage = new Voyage();
        $form = $this->createForm(VoyageType::class, $voyage);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($voyage);
            $em->flush();
            $this->addFlash('success', 'Voyage créé avec succès !');
            return $this->redirectToRoute('admin_voyage_index');
        }

        return $this->render('admin/voyage/new.html.twig', [
            'form' => $form->createView(),
            'voyage' => $voyage,
        ]);
    }

    #[Route('/voyage/{id}', name: 'admin_voyage_show', requirements: ['id' => '\d+'])]
    public function voyageShow(Request $request, Voyage $voyage): Response
    {
        $this->checkRole($request, 'ADMIN');
        return $this->render('admin/voyage/show.html.twig', [
            'voyage' => $voyage,
        ]);
    }

    #[Route('/voyage/{id}/edit', name: 'admin_voyage_edit', requirements: ['id' => '\d+'])]
    public function voyageEdit(Request $request, Voyage $voyage, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        $form = $this->createForm(VoyageType::class, $voyage);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $voyage->setUpdatedAt(new \DateTime());
            $em->flush();
            $this->addFlash('success', 'Voyage modifié avec succès !');
            return $this->redirectToRoute('admin_voyage_index');
        }

        return $this->render('admin/voyage/edit.html.twig', [
            'form' => $form->createView(),
            'voyage' => $voyage,
        ]);
    }

    #[Route('/voyage/{id}/delete', name: 'admin_voyage_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    public function voyageDelete(Request $request, Voyage $voyage, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        if ($this->isCsrfTokenValid('delete' . $voyage->getId(), $request->request->get('_token'))) {
            $em->remove($voyage);
            $em->flush();
            $this->addFlash('success', 'Voyage supprimé avec succès !');
        }
        return $this->redirectToRoute('admin_voyage_index');
    }

    // ===================== RESERVATION CRUD =====================

    #[Route('/reservation', name: 'admin_reservation_index')]
    public function reservationIndex(ReservationVoyageRepository $reservationRepository, Request $request): Response
    {
        $this->checkRole($request, 'ADMIN');
        $search = $request->query->get('search', '');
        $statut = $request->query->get('statut', '');

        if ($search || $statut) {
            $reservations = $reservationRepository->search($search, $statut ?: null);
        } else {
            $reservations = $reservationRepository->findAllWithVoyage();
        }
        $stats = $reservationRepository->getStats();

        return $this->render('admin/reservation/index.html.twig', [
            'reservations' => $reservations,
            'stats' => $stats,
            'search' => $search,
            'currentStatut' => $statut,
        ]);
    }

    #[Route('/reservation/new', name: 'admin_reservation_new')]
    public function reservationNew(Request $request, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        $reservation = new ReservationVoyage();
        $form = $this->createForm(ReservationVoyageType::class, $reservation, ['user_role' => 'ADMIN']);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $reservation->calculerMontantTotal();
            $em->persist($reservation);
            $em->flush();
            $this->addFlash('success', 'Réservation créée avec succès !');
            return $this->redirectToRoute('admin_reservation_index');
        }

        return $this->render('admin/reservation/new.html.twig', [
            'form' => $form->createView(),
        ]);
    }

    #[Route('/reservation/{id}', name: 'admin_reservation_show', requirements: ['id' => '\d+'])]
    public function reservationShow(Request $request, ReservationVoyage $reservation): Response
    {
        $this->checkRole($request, 'ADMIN');
        return $this->render('admin/reservation/show.html.twig', [
            'reservation' => $reservation,
        ]);
    }

    #[Route('/reservation/{id}/edit', name: 'admin_reservation_edit', requirements: ['id' => '\d+'])]
    public function reservationEdit(Request $request, ReservationVoyage $reservation, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        $form = $this->createForm(ReservationVoyageType::class, $reservation, ['user_role' => 'ADMIN']);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $reservation->calculerMontantTotal();
            $em->flush();
            $this->addFlash('success', 'Réservation modifiée avec succès !');
            return $this->redirectToRoute('admin_reservation_index');
        }

        return $this->render('admin/reservation/edit.html.twig', [
            'form' => $form->createView(),
            'reservation' => $reservation,
        ]);
    }

    #[Route('/reservation/{id}/delete', name: 'admin_reservation_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    public function reservationDelete(Request $request, ReservationVoyage $reservation, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        if ($this->isCsrfTokenValid('delete' . $reservation->getId(), $request->request->get('_token'))) {
            $em->remove($reservation);
            $em->flush();
            $this->addFlash('success', 'Réservation supprimée avec succès !');
        }
        return $this->redirectToRoute('admin_reservation_index');
    }

    #[Route('/reservation/{id}/status/{statut}', name: 'admin_reservation_status', requirements: ['id' => '\d+'])]
    public function reservationStatus(Request $request, ReservationVoyage $reservation, string $statut, EntityManagerInterface $em): Response
    {
        $this->checkRole($request, 'ADMIN');
        $validStatuts = ['EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE'];
        if (in_array($statut, $validStatuts)) {
            $reservation->setStatut($statut);
            $em->flush();
            $this->addFlash('success', 'Statut mis à jour : ' . $reservation->getStatutLabel());
        }
        return $this->redirectToRoute('admin_reservation_index');
    }

    private function checkRole(Request $request, string $expectedRole): void
    {
        $role = $request->getSession()->get('user_role', 'CLIENT');
        if ($role !== $expectedRole) {
            $request->getSession()->set('user_role', $expectedRole);
            $request->getSession()->set('user_id', 1);
            $request->getSession()->set('user_name', 'Administrateur');
        }
    }
}

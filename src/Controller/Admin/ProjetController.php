<?php

namespace App\Controller\Admin;

use App\Entity\Projet;
use App\Repository\ProjetRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/admin/projet')]
class ProjetController extends AbstractController
{
    #[Route('/', name: 'app_admin_projet_index', methods: ['GET'])]
    public function index(Request $request, ProjetRepository $repository): Response
    {
        $search = $request->query->get('search');
        $sortBy = $request->query->get('sort', 'id');
        $order = $request->query->get('order', 'ASC');
        $projets = $repository->searchAndSort($search, $sortBy, $order);
        return $this->render('admin/projet/index.html.twig', [
            'projets' => $projets,
            'search' => $search,
            'sortBy' => $sortBy,
            'order' => $order,
        ]);
    }

    #[Route('/{id}', name: 'app_admin_projet_show', requirements: ['id' => '\d+'], methods: ['GET'])]
    public function show(Projet $projet): Response
    {
        return $this->render('admin/projet/show.html.twig', ['projet' => $projet]);
    }

    #[Route('/{id}/delete', name: 'app_admin_projet_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    public function delete(Request $request, Projet $projet, EntityManagerInterface $em): Response
    {
        if ($this->isCsrfTokenValid('admin_delete' . $projet->getId(), (string) $request->request->get('_token'))) {
            $em->remove($projet);
            $em->flush();
            $this->addFlash('success', 'Projet supprimé.');
        }
        return $this->redirectToRoute('app_admin_projet_index');
    }
}

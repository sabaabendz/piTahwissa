<?php

namespace App\Controller\Admin;

use App\Entity\Tache;
use App\Repository\TacheRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/admin/tache')]
class TacheController extends AbstractController
{
    #[Route('/', name: 'app_admin_tache_index', methods: ['GET'])]
    public function index(Request $request, TacheRepository $repository): Response
    {
        $search = $request->query->get('search');
        $projetId = $request->query->get('projet') ? (int) $request->query->get('projet') : null;
        $sortBy = $request->query->get('sort', 'id');
        $order = $request->query->get('order', 'ASC');
        $taches = $repository->searchAndSort($search, $projetId, $sortBy, $order);
        return $this->render('admin/tache/index.html.twig', [
            'taches' => $taches,
            'search' => $search,
            'projetId' => $projetId,
            'sortBy' => $sortBy,
            'order' => $order,
        ]);
    }

    #[Route('/{id}', name: 'app_admin_tache_show', requirements: ['id' => '\d+'], methods: ['GET'])]
    public function show(Tache $tache): Response
    {
        return $this->render('admin/tache/show.html.twig', ['tache' => $tache]);
    }

    #[Route('/{id}/delete', name: 'app_admin_tache_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    public function delete(Request $request, Tache $tache, EntityManagerInterface $em): Response
    {
        if ($this->isCsrfTokenValid('admin_delete' . $tache->getId(), (string) $request->request->get('_token'))) {
            $em->remove($tache);
            $em->flush();
            $this->addFlash('success', 'Tâche supprimée.');
        }
        return $this->redirectToRoute('app_admin_tache_index');
    }
}

<?php

namespace App\Controller;

use App\Entity\Tache;
use App\Form\TacheType;
use App\Repository\ProjetRepository;
use App\Repository\TacheRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[IsGranted('ROLE_USER')]
#[Route('/tache')]
class TacheController extends AbstractController
{
    #[Route('/', name: 'app_tache_index', methods: ['GET'])]
    public function index(TacheRepository $repository): Response
    {
        $taches = $repository->findBy([], ['dateLimite' => 'ASC']);
        return $this->render('front/tache/index.html.twig', ['taches' => $taches]);
    }

    #[Route('/new', name: 'app_tache_new', methods: ['GET', 'POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can create tasks.')]
    public function new(Request $request, EntityManagerInterface $em, ProjetRepository $projetRepository): Response
    {
        $tache = new Tache();
        $projetId = $request->query->get('projet');
        if ($projetId && ($projet = $projetRepository->find((int) $projetId))) {
            $tache->setProjet($projet);
        }
        $form = $this->createForm(TacheType::class, $tache);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($tache);
            $em->flush();
            $this->addFlash('success', 'Tâche créée avec succès.');
            return $this->redirectToRoute('app_tache_show', ['id' => $tache->getId()]);
        }
        
        if ($form->isSubmitted() && !$form->isValid()) {
            $this->addFlash('error', 'Veuillez corriger les erreurs dans le formulaire.');
        }
        return $this->render('front/tache/new.html.twig', ['tache' => $tache, 'form' => $form]);
    }

    #[Route('/{id}', name: 'app_tache_show', requirements: ['id' => '\d+'], methods: ['GET'])]
    public function show(Tache $tache): Response
    {
        return $this->render('front/tache/show.html.twig', ['tache' => $tache]);
    }

    #[Route('/{id}/edit', name: 'app_tache_edit', requirements: ['id' => '\d+'], methods: ['GET', 'POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can edit tasks.')]
    public function edit(Request $request, Tache $tache, EntityManagerInterface $em): Response
    {
        $form = $this->createForm(TacheType::class, $tache);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->flush();
            $this->addFlash('success', 'Tâche mise à jour.');
            return $this->redirectToRoute('app_tache_show', ['id' => $tache->getId()]);
        }
        return $this->render('front/tache/edit.html.twig', ['tache' => $tache, 'form' => $form]);
    }

    #[Route('/{id}/delete', name: 'app_tache_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can delete tasks.')]
    public function delete(Request $request, Tache $tache, EntityManagerInterface $em): Response
    {
        if ($this->isCsrfTokenValid('delete' . $tache->getId(), (string) $request->request->get('_token'))) {
            $em->remove($tache);
            $em->flush();
            $this->addFlash('success', 'Tâche supprimée.');
        }
        return $this->redirectToRoute('app_tache_index');
    }
}

<?php

namespace App\Controller;

use App\Entity\Projet;
use App\Form\ProjetType;
use App\Repository\ProjetRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[IsGranted('ROLE_USER')]
#[Route('/projet')]
class ProjetController extends AbstractController
{
    #[Route('/', name: 'app_projet_index', methods: ['GET'])]
    public function index(ProjetRepository $repository): Response
    {
        $projets = $repository->findBy([], ['dateDebut' => 'DESC']);
        return $this->render('front/projet/index.html.twig', ['projets' => $projets]);
    }

    #[Route('/new', name: 'app_projet_new', methods: ['GET', 'POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can create projects.')]
    public function new(Request $request, EntityManagerInterface $em): Response
    {
        $projet = new Projet();
        $form = $this->createForm(ProjetType::class, $projet);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->persist($projet);
            $em->flush();
            $this->addFlash('success', 'Projet créé avec succès.');
            return $this->redirectToRoute('app_projet_show', ['id' => $projet->getId()]);
        }
        return $this->render('front/projet/new.html.twig', ['projet' => $projet, 'form' => $form]);
    }

    #[Route('/{id}', name: 'app_projet_show', requirements: ['id' => '\d+'], methods: ['GET'])]
    public function show(Projet $projet): Response
    {
        return $this->render('front/projet/show.html.twig', ['projet' => $projet]);
    }

    #[Route('/{id}/edit', name: 'app_projet_edit', requirements: ['id' => '\d+'], methods: ['GET', 'POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can edit projects.')]
    public function edit(Request $request, Projet $projet, EntityManagerInterface $em): Response
    {
        $form = $this->createForm(ProjetType::class, $projet);
        $form->handleRequest($request);
        if ($form->isSubmitted() && $form->isValid()) {
            $em->flush();
            $this->addFlash('success', 'Projet mis à jour.');
            return $this->redirectToRoute('app_projet_show', ['id' => $projet->getId()]);
        }
        return $this->render('front/projet/edit.html.twig', ['projet' => $projet, 'form' => $form]);
    }

    #[Route('/{id}/delete', name: 'app_projet_delete', requirements: ['id' => '\d+'], methods: ['POST'])]
    #[IsGranted('ROLE_MANAGER', message: 'Only managers can delete projects.')]
    public function delete(Request $request, Projet $projet, EntityManagerInterface $em): Response
    {
        if ($this->isCsrfTokenValid('delete' . $projet->getId(), (string) $request->request->get('_token'))) {
            $em->remove($projet);
            $em->flush();
            $this->addFlash('success', 'Projet supprimé.');
        }
        return $this->redirectToRoute('app_projet_index');
    }
}

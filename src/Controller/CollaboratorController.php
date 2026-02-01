<?php

namespace App\Controller;

use App\Entity\Collaborator;
use App\Form\CollaboratorType;
use App\Repository\CollaboratorRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;

#[Route('/collaborator')]
final class CollaboratorController extends AbstractController
{
    #[Route(name: 'app_collaborator_index', methods: ['GET'])]
    public function index(CollaboratorRepository $collaboratorRepository): Response
    {
        return $this->render('collaborator/index.html.twig', [
            'collaborators' => $collaboratorRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_collaborator_new', methods: ['GET', 'POST'])]
    public function new(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $collaborator = new Collaborator();
        $form = $this->createForm(CollaboratorType::class, $collaborator);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $hashedPassword = $passwordHasher->hashPassword(
                $collaborator,
                $collaborator->getPassword()
            );
            $collaborator->setPassword($hashedPassword);

            $entityManager->persist($collaborator);
            $entityManager->flush();

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/new.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }

    #[Route('/{idUser}', name: 'app_collaborator_show', methods: ['GET'])]
    public function show(Collaborator $collaborator): Response
    {
        return $this->render('collaborator/show.html.twig', [
            'collaborator' => $collaborator,
        ]);
    }

    #[Route('/{idUser}/edit', name: 'app_collaborator_edit', methods: ['GET', 'POST'])]
    public function edit(
        Request $request,
        Collaborator $collaborator,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $originalPassword = $collaborator->getPassword();

        $form = $this->createForm(CollaboratorType::class, $collaborator);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            if ($collaborator->getPassword() !== $originalPassword) {
                $hashedPassword = $passwordHasher->hashPassword(
                    $collaborator,
                    $collaborator->getPassword()
                );
                $collaborator->setPassword($hashedPassword);
            }

            $entityManager->flush();

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/edit.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }

    #[Route('/{idUser}', name: 'app_collaborator_delete', methods: ['POST'])]
    public function delete(
        Request $request,
        Collaborator $collaborator,
        EntityManagerInterface $entityManager
    ): Response {
        if ($this->isCsrfTokenValid('delete'.$collaborator->getIdUser(), $request->getPayload()->getString('_token'))) {
            $entityManager->remove($collaborator);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
    }
}

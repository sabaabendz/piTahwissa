<?php

namespace App\Controller;

use App\Entity\Collaborator;
use App\Entity\Manager;
use App\Form\CollaboratorType;
use App\Repository\CollaboratorRepository;
use App\Repository\ManagerRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;
use App\Service\SecurityHelper;

final class CollaboratorController extends AbstractController
{
    public function __construct(
        private readonly SecurityHelper $securityHelper
    ) {
    }
    /**
     * Web route: List all collaborators (filtered by enterprise_code for tenant isolation)
     */
    #[Route('/collaborator', name: 'app_collaborator_index', methods: ['GET'])]
    public function index(CollaboratorRepository $collaboratorRepository): Response
    {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to view collaborators.');
        }

        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to view collaborators.');
        }

        // Get enterprise code based on user type
        $enterpriseCode = $this->securityHelper->getEnterpriseCode($user);

        if (!$enterpriseCode) {
            throw $this->createAccessDeniedException('No enterprise code found for your account.');
        }

        // Filter collaborators by enterprise code (tenant isolation)
        $collaborators = $collaboratorRepository->findByEnterpriseCode($enterpriseCode);

        return $this->render('collaborator/index.html.twig', [
            'collaborators' => $collaborators,
        ]);
    }



    /**
     * Web route: Create new collaborator (form)
     */
    #[Route('/collaborator/new', name: 'app_collaborator_new', methods: ['GET', 'POST'])]
    public function new(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher,
        ManagerRepository $managerRepository
    ): Response {
        $collaborator = new Collaborator();
        $form = $this->createForm(CollaboratorType::class, $collaborator, ['is_edit' => false]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {


            // Hash password from form
            $plainPassword = $form->get('password')->getData();
            $hashedPassword = $passwordHasher->hashPassword(
                $collaborator,
                $plainPassword
            );
            $collaborator->setPassword($hashedPassword);

            $entityManager->persist($collaborator);
            $entityManager->flush();

            $this->addFlash('success', 'Collaborator created successfully!');

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/new.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }



    /**
     * Web route: Show collaborator details (with tenant isolation)
     */
    #[Route('/collaborator/{idUser}', name: 'app_collaborator_show', methods: ['GET'])]
    public function show(Collaborator $collaborator): Response
    {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to view collaborator details.');
        }

        // Tenant isolation: Ensure the collaborator belongs to the same enterprise
        if (!$this->securityHelper->canAccessCollaborator($user, $collaborator)) {
            throw $this->createAccessDeniedException('You do not have access to this collaborator.');
        }

        return $this->render('collaborator/show.html.twig', [
            'collaborator' => $collaborator,
        ]);
    }



    /**
     * Web route: Edit collaborator (with tenant isolation)
     */
    #[Route('/collaborator/{idUser}/edit', name: 'app_collaborator_edit', methods: ['GET', 'POST'])]
    public function edit(
        Request $request,
        Collaborator $collaborator,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $user = $this->getUser();
        
        if (!$user) {
            throw $this->createAccessDeniedException('You must be logged in to edit collaborators.');
        }

        // Tenant isolation: Ensure the collaborator belongs to the same enterprise
        if (!$this->securityHelper->canAccessCollaborator($user, $collaborator)) {
            throw $this->createAccessDeniedException('You do not have access to edit this collaborator.');
        }

        $form = $this->createForm(CollaboratorType::class, $collaborator, ['is_edit' => true]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Only hash password if a new one was provided
            $plainPassword = $form->get('password')->getData();
            if ($plainPassword) {
                $hashedPassword = $passwordHasher->hashPassword(
                    $collaborator,
                    $plainPassword
                );
                $collaborator->setPassword($hashedPassword);
            }

            $entityManager->flush();

            $this->addFlash('success', 'Collaborator updated successfully!');

            return $this->redirectToRoute('app_collaborator_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('collaborator/edit.html.twig', [
            'collaborator' => $collaborator,
            'form' => $form,
        ]);
    }
}

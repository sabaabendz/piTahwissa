<?php

namespace App\Controller;

use App\Entity\Manager;
use App\Form\ManagerType;
use App\Repository\ManagerRepository;
use App\Service\EnterpriseCodeGenerator;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Validator\Validator\ValidatorInterface;

final class ManagerController extends AbstractController
{
    public function __construct(
        private readonly EnterpriseCodeGenerator $enterpriseCodeGenerator
    ) {
    }

    /**
     * Web route: List all managers
     * NOTE: This lists ALL managers globally. Intended for Super Admin or Debugging use only.
     * In a multi-tenant environment, normal users should not see this.
     */
    #[Route('/manager', name: 'app_manager_index', methods: ['GET'])]
    public function index(ManagerRepository $managerRepository): Response
    {
        $managers = $managerRepository->findAll();

        return $this->render('manager/index.html.twig', [
            'managers' => $managers,
        ]);
    }



    /**
     * Web route: Create new manager (form)
     */
    #[Route('/manager/new', name: 'app_manager_new', methods: ['GET', 'POST'])]
    public function new(
        Request $request,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $manager = new Manager();
        $form = $this->createForm(ManagerType::class, $manager, ['is_edit' => false]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Generate enterprise code
            $enterpriseCode = $this->enterpriseCodeGenerator->generate();
            $manager->setEnterpriseCode($enterpriseCode);

            // Hash password from form
            $plainPassword = $form->get('password')->getData();
            $hashedPassword = $passwordHasher->hashPassword(
                $manager,
                $plainPassword
            );
            $manager->setPassword($hashedPassword);

            $entityManager->persist($manager);
            $entityManager->flush();

            $this->addFlash('success', 'Manager created successfully!');

            return $this->redirectToRoute('app_manager_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('manager/new.html.twig', [
            'manager' => $manager,
            'form' => $form,
        ]);
    }



    /**
     * Web route: Show manager details
     */
    #[Route('/manager/{idUser}', name: 'app_manager_show', methods: ['GET'])]
    public function show(Manager $manager): Response
    {
        return $this->render('manager/show.html.twig', [
            'manager' => $manager,
        ]);
    }



    /**
     * Web route: Edit manager
     */
    #[Route('/manager/{idUser}/edit', name: 'app_manager_edit', methods: ['GET', 'POST'])]
    public function edit(
        Request $request,
        Manager $manager,
        EntityManagerInterface $entityManager,
        UserPasswordHasherInterface $passwordHasher
    ): Response {
        $form = $this->createForm(ManagerType::class, $manager, ['is_edit' => true]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Only hash password if a new one was provided
            $plainPassword = $form->get('password')->getData();
            if ($plainPassword) {
                $hashedPassword = $passwordHasher->hashPassword(
                    $manager,
                    $plainPassword
                );
                $manager->setPassword($hashedPassword);
            }

            $entityManager->flush();

            $this->addFlash('success', 'Manager updated successfully!');

            return $this->redirectToRoute('app_manager_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('manager/edit.html.twig', [
            'manager' => $manager,
            'form' => $form,
        ]);
    }
}

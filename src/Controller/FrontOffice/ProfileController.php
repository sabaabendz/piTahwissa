<?php

namespace App\Controller\FrontOffice;

use App\Entity\User;
use App\Form\UserProfileType;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[IsGranted('ROLE_USER')]
class ProfileController extends AbstractController
{
    #[Route('/profile/edit', name: 'app_front_profile_edit')]
    public function edit(
        Request $request,
        EntityManagerInterface $entityManager,
    ): Response {
        $user = $this->getUser();

        if (!$user instanceof User) {
            throw $this->createAccessDeniedException('Authenticated user not found.');
        }

        $form = $this->createForm(UserProfileType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            $this->addFlash('success', 'Profile updated successfully.');

            return $this->redirectToRoute('app_front_profile_edit');
        }

        return $this->render('frontoffice/profile/edit.html.twig', [
            'form' => $form->createView(),
        ]);
    }
}

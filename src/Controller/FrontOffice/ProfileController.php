<?php

namespace App\Controller\FrontOffice;

use App\Form\UserProfileType;
use App\Service\FaceRecognitionService;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[IsGranted('ROLE_USER')]
class ProfileController extends AbstractController
{
    #[Route('/profile/edit', name: 'app_front_profile_edit')]
    public function edit(
        Request $request, 
        EntityManagerInterface $entityManager, 
        UserPasswordHasherInterface $passwordHasher,
        FaceRecognitionService $faceRecognitionService,
        LoggerInterface $logger,
    ): Response {
        $user = $this->getUser();
        $form = $this->createForm(UserProfileType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = $form->get('plainPassword')->getData();
            if ($plainPassword) {
                $user->setPassword(
                    $passwordHasher->hashPassword($user, $plainPassword)
                );
            }

            $faceImageBase64 = (string) $request->request->get('face_image_base64', '');
            if ('' !== trim($faceImageBase64)) {
                try {
                    $embedding = $faceRecognitionService->enroll($faceImageBase64);
                    $user->setFaceEmbedding($embedding);
                    $this->addFlash('success', 'Face ID configured successfully.');
                } catch (\Throwable $e) {
                    $logger->warning('Face ID enrollment failed', [
                        'user' => method_exists($user, 'getEmail') ? $user->getEmail() : null,
                        'error' => $e->getMessage(),
                    ]);

                    $this->addFlash('error', $e->getMessage());

                    return $this->redirectToRoute('app_front_profile_edit');
                }
            }

            $entityManager->flush();

            $this->addFlash('success', 'Votre profil a été mis à jour avec succès.');

            return $this->redirectToRoute('app_front_profile_edit');
        }

        return $this->render('frontoffice/profile/edit.html.twig', [
            'form' => $form->createView(),
        ]);
    }
}

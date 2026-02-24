<?php

namespace App\Controller;

use App\Entity\Projet;
use App\Entity\Tache;
use App\Service\GroqAiService;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;

class AiSuggestionController extends AbstractController
{
    #[Route('/api/ai/suggest-tasks', name: 'ai_suggest_tasks', methods: ['POST'])]
    public function suggestTasks(Request $request, GroqAiService $aiService): JsonResponse
    {
        try {
            $data = json_decode($request->getContent(), true);
            $description = $data['description'] ?? '';
            $projectName = $data['projectName'] ?? '';

            if (empty($description)) {
                return $this->json(['error' => 'Description is required'], 400);
            }

            $suggestions = $aiService->suggestTasks($description, $projectName);

            return $this->json(['suggestions' => $suggestions]);
        } catch (\Exception $e) {
            return $this->json([
                'error' => 'Error generating suggestions',
                'message' => $e->getMessage()
            ], 500);
        }
    }

    #[Route('/api/ai/accept-task/{projetId}', name: 'ai_accept_task', methods: ['POST'])]
    public function acceptTask(
        int $projetId,
        Request $request,
        EntityManagerInterface $em
    ): JsonResponse {
        $projet = $em->getRepository(Projet::class)->find($projetId);
        
        if (!$projet) {
            return $this->json(['error' => 'Project not found'], 404);
        }

        $data = json_decode($request->getContent(), true);
        
        $tache = new Tache();
        $tache->setLibelle($data['libelle'] ?? '');
        $tache->setPriorite($data['priorite'] ?? Tache::PRIORITE_MOYENNE);
        
        $days = $data['days'] ?? 7;
        $dateLimite = (clone $projet->getDateDebut())->modify("+{$days} days");
        $tache->setDateLimite($dateLimite);
        $tache->setEtat(Tache::ETAT_A_FAIRE);
        $tache->setProjet($projet);

        $em->persist($tache);
        $em->flush();

        return $this->json([
            'success' => true,
            'message' => 'Task created successfully',
            'task' => [
                'id' => $tache->getId(),
                'libelle' => $tache->getLibelle(),
                'priorite' => $tache->getPriorite(),
                'dateLimite' => $tache->getDateLimite()->format('Y-m-d'),
            ]
        ]);
    }
}

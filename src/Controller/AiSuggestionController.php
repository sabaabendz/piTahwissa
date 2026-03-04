<?php

namespace App\Controller;

use App\Entity\Projet;
use App\Entity\Tache;
use App\Service\GroqAiService;
use Doctrine\ORM\EntityManagerInterface;
use Psr\Log\LoggerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;

class AiSuggestionController extends AbstractController
{
    public function __construct(private readonly LoggerInterface $logger)
    {
    }

    #[Route('/api/ai/suggest-tasks', name: 'ai_suggest_tasks', methods: ['POST'])]
    public function suggestTasks(Request $request, GroqAiService $aiService): JsonResponse
    {
        try {
            $data = json_decode($request->getContent(), true);

            if (!is_array($data)) {
                return $this->json(['error' => 'Invalid JSON payload'], 400);
            }

            $description = $data['description'] ?? '';
            $projectName = $data['projectName'] ?? '';

            if (empty($description)) {
                return $this->json(['error' => 'Description is required'], 400);
            }

            $suggestions = $aiService->suggestTasks($description, $projectName);

            if (!is_array($suggestions)) {
                return $this->json(['error' => 'AI service returned invalid data'], 500);
            }

            return $this->json(['suggestions' => $suggestions]);
        } catch (\Throwable $e) {
            $this->logger->error('AI suggestion failed', [
                'error' => $e->getMessage(),
                'trace' => $e->getTraceAsString(),
            ]);

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
        try {
            $projet = $em->getRepository(Projet::class)->find($projetId);

            if (!$projet) {
                return $this->json(['error' => 'Project not found'], 404);
            }

            $data = json_decode($request->getContent(), true);
            if (!is_array($data)) {
                return $this->json(['error' => 'Invalid JSON payload'], 400);
            }

            $libelle = trim((string) ($data['libelle'] ?? ''));
            if ($libelle === '') {
                return $this->json(['error' => 'Task label is required'], 400);
            }

            $tache = new Tache();
            $tache->setLibelle($libelle);
            $tache->setPriorite($data['priorite'] ?? Tache::PRIORITE_MOYENNE);

            $days = max(1, (int) ($data['days'] ?? 7));
            $startDate = $projet->getDateDebut() ?? new \DateTimeImmutable('today');
            $dateLimite = (clone $startDate)->modify("+{$days} days");
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
        } catch (\Throwable $e) {
            $this->logger->error('AI accept task failed', [
                'error' => $e->getMessage(),
                'projectId' => $projetId,
            ]);

            return $this->json([
                'error' => 'Error creating task',
                'message' => $e->getMessage(),
            ], 500);
        }
    }
}

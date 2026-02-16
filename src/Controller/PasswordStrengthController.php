<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\HttpKernel\Exception\TooManyRequestsHttpException;
use Symfony\Component\RateLimiter\RateLimiterFactory;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Component\DependencyInjection\Attribute\Target;

class PasswordStrengthController extends AbstractController
{
    private const ML_SERVICE_URL = 'http://127.0.0.1:8001/predict';

    public function __construct(
        private readonly HttpClientInterface $httpClient,
        #[Target('limiter.password_strength_limiter')]
        private readonly RateLimiterFactory $passwordStrengthLimiter
    ) {
    }

    #[Route('/api/password-strength', name: 'api_password_strength', methods: ['POST'])]
    public function checkStrength(Request $request): JsonResponse
    {
        $limiter = $this->passwordStrengthLimiter->create($request->getClientIp());
        if (false === $limiter->consume(1)->isAccepted()) {
            return new JsonResponse(['error' => 'Too many requests'], 429);
        }

        $data = json_decode($request->getContent(), true);
        $password = $data['password'] ?? null;

        if (!$password) {
            return new JsonResponse(['error' => 'Password is required'], 400);
        }

        // Validate password length
        if (strlen($password) > 128) {
            return new JsonResponse(['error' => 'Password too long'], 400);
        }

        try {
            $response = $this->httpClient->request('POST', self::ML_SERVICE_URL, [
                'json' => ['password' => $password],
                'timeout' => 3.0, // Slightly increased for new model processing
            ]);

            if ($response->getStatusCode() !== 200) {
                return new JsonResponse([
                    'error' => 'ML service error',
                    // Fallback response
                    'score' => 50,
                    'label' => 'medium',
                    'suggestions' => ['Unable to analyze password strength. Please try again.']
                ], 502);
            }

            $mlResponse = $response->toArray();
            
            // NEW: The API now returns richer data structure
            // Expected format:
            // {
            //   "score": 75.5,
            //   "label": "strong",
            //   "confidence": 85.2,
            //   "suggestions": ["..."],
            //   "details": {...}
            // }
            
            return new JsonResponse($mlResponse);

        } catch (\Exception $e) {
            // Fallback response if ML service is down
            // This allows registration to continue without blocking users
            return new JsonResponse([
                'score' => 50,
                'label' => 'medium',
                'confidence' => 50,
                'suggestions' => ['Password strength check temporarily unavailable.'],
                'error' => 'Service temporarily unavailable'
            ], 503);
        }
    }
}
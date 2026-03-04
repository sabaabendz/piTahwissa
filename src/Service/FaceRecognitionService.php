<?php

namespace App\Service;

use Psr\Log\LoggerInterface;
use Symfony\Contracts\HttpClient\Exception\ExceptionInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;

class FaceRecognitionService
{
    public function __construct(
        private readonly HttpClientInterface $httpClient,
        private readonly LoggerInterface $logger,
        private readonly string $baseUrl = 'http://127.0.0.1:8001',
        private readonly float $timeout = 6.0,
    ) {
    }

    public function enroll(string $imageBase64): array
    {
        $this->assertValidImage($imageBase64);

        $data = $this->postJson('/face/enroll', [
            'image' => $imageBase64,
        ]);

        if (!isset($data['embedding']) || !is_array($data['embedding'])) {
            throw new \RuntimeException('Invalid enroll response from Face ID service.');
        }

        return $data['embedding'];
    }

    public function verify(string $imageBase64, array $storedEmbedding): array
    {
        $this->assertValidImage($imageBase64);

        if ($storedEmbedding === []) {
            throw new \InvalidArgumentException('Stored embedding is empty.');
        }

        $data = $this->postJson('/face/verify', [
            'image' => $imageBase64,
            'stored_embedding' => $storedEmbedding,
        ]);

        if (!isset($data['similarity'], $data['match'])) {
            throw new \RuntimeException('Invalid verify response from Face ID service.');
        }

        return [
            'similarity' => (float) $data['similarity'],
            'match' => (bool) $data['match'],
        ];
    }

    private function assertValidImage(string $imageBase64): void
    {
        if ('' === trim($imageBase64)) {
            throw new \InvalidArgumentException('Image is required.');
        }

        if (!str_starts_with($imageBase64, 'data:image/') || !str_contains($imageBase64, ',')) {
            throw new \InvalidArgumentException('Invalid image format.');
        }
    }

    private function postJson(string $path, array $payload): array
    {
        try {
            $response = $this->httpClient->request('POST', rtrim($this->baseUrl, '/') . $path, [
                'json' => $payload,
                'timeout' => $this->timeout,
            ]);

            $statusCode = $response->getStatusCode();
            $content = $response->toArray(false);

            if ($statusCode >= 400) {
                $detail = $content['detail'] ?? 'Face ID service error.';
                throw new \RuntimeException((string) $detail);
            }

            return $content;
        } catch (ExceptionInterface $e) {
            $this->logger->error('Face ID request failed', [
                'path' => $path,
                'error' => $e->getMessage(),
            ]);

            throw new \RuntimeException('Face ID service is unavailable. Please try again.');
        }
    }
}

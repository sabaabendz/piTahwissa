<?php

namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class GroqAiService
{
    private string $apiKey;
    private HttpClientInterface $httpClient;

    public function __construct(HttpClientInterface $httpClient, string $groqApiKey)
    {
        $this->httpClient = $httpClient;
        $this->apiKey = $groqApiKey;
    }

    public function suggestTasks(string $projectDescription, string $projectName): array
    {
        $prompt = "Based on this project description, suggest 3-5 specific tasks that need to be completed. 
Project Name: {$projectName}
Description: {$projectDescription}

For each task, provide:
1. Task name (short, clear)
2. Priority (basse, moyenne, or haute)
3. Estimated days to complete (1-30)

Return ONLY a valid JSON array with this exact structure:
[{\"libelle\": \"task name\", \"priorite\": \"moyenne\", \"days\": 7}]

Be specific and practical. No explanations, just the JSON array.";

        try {
            $response = $this->httpClient->request('POST', 'https://api.groq.com/openai/v1/chat/completions', [
                'headers' => [
                    'Authorization' => 'Bearer ' . $this->apiKey,
                    'Content-Type' => 'application/json',
                ],
                'json' => [
                    'model' => 'llama-3.3-70b-versatile',
                    'messages' => [
                        ['role' => 'user', 'content' => $prompt]
                    ],
                    'temperature' => 0.7,
                    'max_tokens' => 1000,
                ],
            ]);

            $data = $response->toArray();
            $content = $data['choices'][0]['message']['content'] ?? '';
            
            $content = trim($content);
            if (str_starts_with($content, '```json')) {
                $content = trim(substr($content, 7));
            }
            if (str_starts_with($content, '```')) {
                $content = trim(substr($content, 3));
            }
            if (str_ends_with($content, '```')) {
                $content = trim(substr($content, 0, -3));
            }

            $tasks = json_decode($content, true);
            
            if (!is_array($tasks)) {
                return [];
            }

            return $tasks;
        } catch (\Exception $e) {
            return [];
        }
    }
}

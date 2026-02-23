<?php

namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class RecaptchaVerifier
{
    public function __construct(
        private readonly HttpClientInterface $httpClient,
        private readonly string $secretKey
    ) {
    }

    public function verify(?string $responseToken, ?string $remoteIp = null): bool
    {
        if (!$responseToken || trim($responseToken) === '') {
            return false;
        }

        try {
            $response = $this->httpClient->request('POST', 'https://www.google.com/recaptcha/api/siteverify', [
                'body' => [
                    'secret' => $this->secretKey,
                    'response' => $responseToken,
                    'remoteip' => $remoteIp,
                ],
            ]);

            $data = $response->toArray(false);

            return isset($data['success']) && $data['success'] === true;
        } catch (\Throwable) {
            return false;
        }
    }
}

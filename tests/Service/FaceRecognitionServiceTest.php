<?php

declare(strict_types=1);

namespace App\Tests\Service;

use App\Service\FaceRecognitionService;
use PHPUnit\Framework\TestCase;
use Psr\Log\LoggerInterface;
use Symfony\Contracts\HttpClient\Exception\ExceptionInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;
use Symfony\Contracts\HttpClient\ResponseInterface;

final class FaceRecognitionServiceTest extends TestCase
{
    public function testEnrollReturnsEmbeddingOnSuccess(): void
    {
        $response = $this->createMock(ResponseInterface::class);
        $response->method('getStatusCode')->willReturn(200);
        $response->method('toArray')->with(false)->willReturn([
            'embedding' => [0.1, 0.2, 0.3],
        ]);

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient
            ->expects($this->once())
            ->method('request')
            ->with(
                'POST',
                'http://127.0.0.1:8001/face/enroll',
                [
                    'json' => ['image' => $this->validImage()],
                    'timeout' => 6.0,
                ]
            )
            ->willReturn($response);

        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $embedding = $service->enroll($this->validImage());

        $this->assertSame([0.1, 0.2, 0.3], $embedding);
    }

    public function testEnrollThrowsWhenImageIsEmpty(): void
    {
        $httpClient = $this->createMock(HttpClientInterface::class);
        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\InvalidArgumentException::class);
        $this->expectExceptionMessage('Image is required.');

        $service->enroll('   ');
    }

    public function testEnrollThrowsWhenImageFormatIsInvalid(): void
    {
        $httpClient = $this->createMock(HttpClientInterface::class);
        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\InvalidArgumentException::class);
        $this->expectExceptionMessage('Invalid image format.');

        $service->enroll('not-a-data-uri');
    }

    public function testEnrollThrowsWhenEmbeddingIsMissingInResponse(): void
    {
        $response = $this->createMock(ResponseInterface::class);
        $response->method('getStatusCode')->willReturn(200);
        $response->method('toArray')->with(false)->willReturn(['foo' => 'bar']);

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient->method('request')->willReturn($response);

        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('Invalid enroll response from Face ID service.');

        $service->enroll($this->validImage());
    }

    public function testVerifyReturnsSimilarityAndMatchOnSuccess(): void
    {
        $response = $this->createMock(ResponseInterface::class);
        $response->method('getStatusCode')->willReturn(200);
        $response->method('toArray')->with(false)->willReturn([
            'similarity' => '0.923',
            'match' => 1,
        ]);

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient
            ->expects($this->once())
            ->method('request')
            ->with(
                'POST',
                'http://127.0.0.1:8001/face/verify',
                [
                    'json' => [
                        'image' => $this->validImage(),
                        'stored_embedding' => [0.1, 0.2, 0.3],
                    ],
                    'timeout' => 6.0,
                ]
            )
            ->willReturn($response);

        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $result = $service->verify($this->validImage(), [0.1, 0.2, 0.3]);

        $this->assertSame(0.923, $result['similarity']);
        $this->assertTrue($result['match']);
    }

    public function testVerifyThrowsWhenStoredEmbeddingIsEmpty(): void
    {
        $httpClient = $this->createMock(HttpClientInterface::class);
        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\InvalidArgumentException::class);
        $this->expectExceptionMessage('Stored embedding is empty.');

        $service->verify($this->validImage(), []);
    }

    public function testVerifyThrowsWhenResponseIsMissingKeys(): void
    {
        $response = $this->createMock(ResponseInterface::class);
        $response->method('getStatusCode')->willReturn(200);
        $response->method('toArray')->with(false)->willReturn(['foo' => 'bar']);

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient->method('request')->willReturn($response);

        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('Invalid verify response from Face ID service.');

        $service->verify($this->validImage(), [0.1]);
    }

    public function testThrowsApiDetailWhenStatusCodeIsError(): void
    {
        $response = $this->createMock(ResponseInterface::class);
        $response->method('getStatusCode')->willReturn(400);
        $response->method('toArray')->with(false)->willReturn(['detail' => 'No face detected']);

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient->method('request')->willReturn($response);

        $logger = $this->createMock(LoggerInterface::class);
        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('No face detected');

        $service->enroll($this->validImage());
    }

    public function testThrowsUnavailableAndLogsWhenHttpClientThrowsException(): void
    {
        $httpException = new class ('network') extends \Exception implements ExceptionInterface {
        };

        $httpClient = $this->createMock(HttpClientInterface::class);
        $httpClient
            ->method('request')
            ->willThrowException($httpException);

        $logger = $this->createMock(LoggerInterface::class);
        $logger
            ->expects($this->once())
            ->method('error')
            ->with(
                'Face ID request failed',
                $this->callback(static function (array $context): bool {
                    return isset($context['path'], $context['error'])
                        && $context['path'] === '/face/enroll'
                        && $context['error'] === 'network';
                })
            );

        $service = new FaceRecognitionService($httpClient, $logger);

        $this->expectException(\RuntimeException::class);
        $this->expectExceptionMessage('Face ID service is unavailable. Please try again.');

        $service->enroll($this->validImage());
    }

    private function validImage(): string
    {
        return 'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD';
    }
}

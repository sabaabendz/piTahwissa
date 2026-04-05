<?php

namespace App\EventSubscriber;

use Symfony\Component\EventDispatcher\EventSubscriberInterface;
use Symfony\Component\HttpKernel\Event\ResponseEvent;
use Symfony\Component\HttpKernel\KernelEvents;
use Symfony\Component\Security\Core\Authentication\Token\Storage\TokenStorageInterface;

/**
 * Adds cache-control headers to prevent browser caching of protected pages
 */
class CacheControlSubscriber implements EventSubscriberInterface
{
    public function __construct(
        private readonly TokenStorageInterface $tokenStorage
    ) {
    }

    public static function getSubscribedEvents(): array
    {
        return [
            KernelEvents::RESPONSE => ['onKernelResponse', -10],
        ];
    }

    public function onKernelResponse(ResponseEvent $event): void
    {
        $response = $event->getResponse();
        $request = $event->getRequest();

        // Skip if it's a redirect or already has cache headers set
        if ($response->isRedirection() || $response->headers->hasCacheControlDirective('no-cache')) {
            return;
        }

        // Get the route name
        $route = $request->attributes->get('_route');

        // Protected routes that should not be cached
        $protectedRoutes = [
            'app_dashboard_index',
            'app_dashboard',
            'app_front_profile_edit',
            'app_admin_dashboard',
            'app_admin_users_index',
        ];

        // If user is authenticated and on a protected route, prevent caching
        $token = $this->tokenStorage->getToken();
        if ($token && $token->getUser() && in_array($route, $protectedRoutes, true)) {
            $response->setCache([
                'must_revalidate' => true,
                'no_cache' => true,
                'no_store' => true,
                'max_age' => 0,
            ]);
        }

        // Always prevent caching of login and register pages
        if (in_array($route, ['app_login', 'app_register', 'app_forgot_password'], true)) {
            $response->setCache([
                'must_revalidate' => true,
                'no_cache' => true,
                'no_store' => true,
                'max_age' => 0,
            ]);
        }
    }
}

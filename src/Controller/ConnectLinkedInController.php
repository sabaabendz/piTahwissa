<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;

class ConnectLinkedInController extends AbstractController
{
    /**
     * Redirect to LinkedIn OAuth2 to start the "connect" process.
     */
    #[Route('/connect/linkedin', name: 'connect_linkedin_start', methods: ['GET'])]
    public function connectAction(Request $request, ClientRegistry $clientRegistry): RedirectResponse
    {
        // 1. Check for 'role' parameter
        $role = $request->query->get('role');

        // 2. Validate and store in session if present
        if ($role && in_array($role, ['manager', 'collaborator'])) {
            $request->getSession()->set('_oauth_auth_role', $role);
        }

        return $clientRegistry
            ->getClient('linkedin')
            ->redirect(['r_liteprofile', 'r_emailaddress'], []);
    }

    /**
     * After LinkedIn redirects back here, the authenticator handles the rest.
     * This route must match redirect_route in knpu_oauth2_client.yaml.
     */
    #[Route('/connect/linkedin/check', name: 'connect_linkedin_check', methods: ['GET'])]
    public function connectCheckAction(Request $request, ClientRegistry $clientRegistry): never
    {
        // This controller is never executed. The authenticator handles the OAuth flow.
        throw new \LogicException('This controller should not be reached. Check your security configuration.');
    }
}

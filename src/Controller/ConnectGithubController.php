<?php

namespace App\Controller;

use KnpU\OAuth2ClientBundle\Client\ClientRegistry;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Attribute\Route;

class ConnectGithubController extends AbstractController
{
    /**
     * Redirect to GitHub OAuth2 to start the "connect" process.
     */
    #[Route('/connect/github', name: 'connect_github_start', methods: ['GET'])]
    public function connectAction(Request $request, ClientRegistry $clientRegistry): RedirectResponse
    {
        $role = $request->query->get('role');

        if ($role && in_array($role, ['manager', 'collaborator'], true)) {
            $request->getSession()->set('_oauth_auth_role', $role);
        }

        return $clientRegistry
            ->getClient('github')
            ->redirect(['read:user', 'user:email'], []);
    }

    /**
     * After GitHub redirects back here, the authenticator handles the rest.
     * This route must match redirect_route in knpu_oauth2_client.yaml.
     */
    #[Route('/connect/github/check', name: 'connect_github_check', methods: ['GET'])]
    public function connectCheckAction(Request $request, ClientRegistry $clientRegistry): never
    {
        throw new \LogicException('This controller should not be reached. Check your security configuration.');
    }
}

<?php

namespace App\Service;

use App\Entity\Collaborator;
use App\Entity\Manager;
use Symfony\Component\Security\Core\User\UserInterface;

class SecurityHelper
{
    /**
     * Helper to get the enterprise code from the current user safely.
     */
    public function getEnterpriseCode(?UserInterface $user): ?string
    {
        if (!$user) {
            return null;
        }

        if ($user instanceof Manager) {
            return $user->getEnterpriseCode();
        }

        if ($user instanceof Collaborator) {
            return $user->getEnterpriseCode();
        }

        return null;
    }

    /**
     * Checks if the user is allowed to access the given collaborator resource.
     * Enforces tenant isolation based on enterprise code.
     */
    public function canAccessCollaborator(?UserInterface $user, Collaborator $collaborator): bool
    {
        $userCode = $this->getEnterpriseCode($user);
        $targetCode = $collaborator->getEnterpriseCode();

        if (!$userCode || !$targetCode) {
            return false;
        }

        return $userCode === $targetCode;
    }
}

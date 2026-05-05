<?php

declare(strict_types=1);

namespace App\Tests\User;

use App\Entity\User;
use App\Security\UserEnabledChecker;
use PHPUnit\Framework\TestCase;
use Symfony\Component\Security\Core\Exception\CustomUserMessageAccountStatusException;

class UserEnabledCheckerTest extends TestCase
{
    public function testPreAuthAllowsActiveUsers(): void
    {
        $user = new User();
        $user->setIsActive(true);

        $checker = new UserEnabledChecker();
        $checker->checkPreAuth($user);

        self::assertTrue(true);
    }

    public function testPreAuthBlocksInactiveUsers(): void
    {
        $user = new User();
        $user->setIsActive(false);

        $checker = new UserEnabledChecker();

        $this->expectException(CustomUserMessageAccountStatusException::class);
        $checker->checkPreAuth($user);
    }
}

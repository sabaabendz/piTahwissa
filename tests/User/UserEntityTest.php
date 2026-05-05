<?php

declare(strict_types=1);

namespace App\Tests\User;

use App\Entity\User;
use PHPUnit\Framework\TestCase;

class UserEntityTest extends TestCase
{
    public function testNameFallsBackToEmail(): void
    {
        $user = new User();
        $user->setEmail('user@example.com');

        self::assertSame('user@example.com', $user->getName());
    }

    public function testSetNameSplitsIntoFirstAndLastName(): void
    {
        $user = new User();
        $user->setName('Jane Doe');

        self::assertSame('Jane', $user->getFirstName());
        self::assertSame('Doe', $user->getLastName());
    }

    public function testAvatarUrlIsNormalized(): void
    {
        $user = new User();

        self::assertNull($user->getAvatarUrl());

        $user->setAvatarUrl('avatar.png');
        self::assertSame('/uploads/avatars/avatar.png', $user->getAvatarUrl());

        $user->setAvatarUrl('/uploads/avatars/avatar.png');
        self::assertSame('/uploads/avatars/avatar.png', $user->getAvatarUrl());
    }
}

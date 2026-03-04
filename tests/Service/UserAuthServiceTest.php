<?php

declare(strict_types=1);

namespace App\Tests\Service;

use App\Entity\User;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use PHPUnit\Framework\TestCase;
use PHPUnit\Framework\MockObject\MockObject;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Security\Core\Exception\BadCredentialsException;
use Symfony\Component\Security\Core\Exception\UserNotFoundException;

final class UserAuthServiceTest extends TestCase
{
    private MockObject|UserRepository $userRepository;
    private MockObject|UserPasswordHasherInterface $passwordHasher;
    private MockObject|EntityManagerInterface $entityManager;
    private UserAuthService $authService;

    protected function setUp(): void
    {
        $this->userRepository = $this->createMock(UserRepository::class);
        $this->passwordHasher = $this->createMock(UserPasswordHasherInterface::class);
        $this->entityManager = $this->createMock(EntityManagerInterface::class);

        $this->authService = new UserAuthService(
            $this->userRepository,
            $this->passwordHasher,
            $this->entityManager
        );
    }

    public function testRegistrationSucceedsWithValidEmailAndPassword(): void
    {
        $email = 'new.user@example.com';
        $plainPassword = 'StrongP@ssw0rd!';
        $name = 'New User';

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => $email])
            ->willReturn(null);

        $this->passwordHasher
            ->expects($this->once())
            ->method('hashPassword')
            ->with($this->isInstanceOf(User::class), $plainPassword)
            ->willReturn('hashed_password_value');

        $this->entityManager
            ->expects($this->once())
            ->method('persist')
            ->with($this->callback(static function (User $user) use ($email, $name): bool {
                return $user->getEmail() === $email
                    && $user->getName() === $name
                    && $user->getPassword() === 'hashed_password_value';
            }));

        $this->entityManager
            ->expects($this->once())
            ->method('flush');

        $user = $this->authService->register($email, $plainPassword, $name);

        $this->assertSame($email, $user->getEmail());
        $this->assertSame($name, $user->getName());
        $this->assertSame('hashed_password_value', $user->getPassword());
    }

    public function testRegistrationHashesPasswordBeforeSaving(): void
    {
        $email = 'hash.check@example.com';
        $plainPassword = 'MyPlainPassword123!';

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => $email])
            ->willReturn(null);

        $this->passwordHasher
            ->expects($this->once())
            ->method('hashPassword')
            ->with($this->isInstanceOf(User::class), $plainPassword)
            ->willReturn('secure_hash');

        $this->entityManager
            ->expects($this->once())
            ->method('persist')
            ->with($this->callback(static function (User $user) use ($plainPassword): bool {
                return $user->getPassword() !== $plainPassword
                    && $user->getPassword() === 'secure_hash';
            }));

        $this->entityManager
            ->expects($this->once())
            ->method('flush');

        $registered = $this->authService->register($email, $plainPassword, 'Hash Checker');

        $this->assertSame('secure_hash', $registered->getPassword());
        $this->assertNotSame($plainPassword, $registered->getPassword());
    }

    public function testRegistrationRejectsDuplicateEmail(): void
    {
        $existing = (new User())
            ->setEmail('existing@example.com')
            ->setName('Existing User')
            ->setPassword('already_hashed');

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => 'existing@example.com'])
            ->willReturn($existing);

        $this->passwordHasher
            ->expects($this->never())
            ->method('hashPassword');

        $this->entityManager
            ->expects($this->never())
            ->method('persist');

        $this->entityManager
            ->expects($this->never())
            ->method('flush');

        $this->expectException(\LogicException::class);
        $this->expectExceptionMessage('Email already exists.');

        $this->authService->register('existing@example.com', 'AnyPassword123!', 'Duplicate');
    }

    public function testLoginSucceedsWithCorrectCredentials(): void
    {
        $user = (new User())
            ->setEmail('login.ok@example.com')
            ->setName('Login Ok')
            ->setPassword('hashed_db_value');

        $this->entityManager
            ->expects($this->never())
            ->method('persist');

        $this->entityManager
            ->expects($this->never())
            ->method('flush');

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => 'login.ok@example.com'])
            ->willReturn($user);

        $this->passwordHasher
            ->expects($this->once())
            ->method('isPasswordValid')
            ->with($user, 'CorrectPassword123!')
            ->willReturn(true);

        $authenticated = $this->authService->login('login.ok@example.com', 'CorrectPassword123!');

        $this->assertSame($user, $authenticated);
    }

    public function testLoginThrowsExceptionWhenPasswordIsIncorrect(): void
    {
        $user = (new User())
            ->setEmail('wrong.password@example.com')
            ->setName('Wrong Password')
            ->setPassword('hashed_db_value');

        $this->entityManager
            ->expects($this->never())
            ->method('persist');

        $this->entityManager
            ->expects($this->never())
            ->method('flush');

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => 'wrong.password@example.com'])
            ->willReturn($user);

        $this->passwordHasher
            ->expects($this->once())
            ->method('isPasswordValid')
            ->with($user, 'WrongPassword!')
            ->willReturn(false);

        $this->expectException(BadCredentialsException::class);
        $this->expectExceptionMessage('Invalid password.');

        $this->authService->login('wrong.password@example.com', 'WrongPassword!');
    }

    public function testLoginThrowsExceptionWhenEmailDoesNotExist(): void
    {
        $this->entityManager
            ->expects($this->never())
            ->method('persist');

        $this->entityManager
            ->expects($this->never())
            ->method('flush');

        $this->userRepository
            ->expects($this->once())
            ->method('findOneBy')
            ->with(['email' => 'missing@example.com'])
            ->willReturn(null);

        $this->passwordHasher
            ->expects($this->never())
            ->method('isPasswordValid');

        $this->expectException(UserNotFoundException::class);
        $this->expectExceptionMessage('User not found.');

        $this->authService->login('missing@example.com', 'AnyPassword123!');
    }
}

final class UserAuthService
{
    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly UserPasswordHasherInterface $passwordHasher,
        private readonly EntityManagerInterface $entityManager,
    ) {
    }

    public function register(string $email, string $plainPassword, string $name): User
    {
        $existing = $this->userRepository->findOneBy(['email' => $email]);
        if ($existing instanceof User) {
            throw new \LogicException('Email already exists.');
        }

        $user = new User();
        $user->setEmail($email);
        $user->setName($name);
        $user->setPassword($this->passwordHasher->hashPassword($user, $plainPassword));

        $this->entityManager->persist($user);
        $this->entityManager->flush();

        return $user;
    }

    public function login(string $email, string $plainPassword): User
    {
        $user = $this->userRepository->findOneBy(['email' => $email]);

        if (!$user instanceof User) {
            throw new UserNotFoundException('User not found.');
        }

        if (!$this->passwordHasher->isPasswordValid($user, $plainPassword)) {
            throw new BadCredentialsException('Invalid password.');
        }

        return $user;
    }
}

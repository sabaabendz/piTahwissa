<?php

namespace App\Entity;

use App\Repository\UserRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\HttpFoundation\File\File;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Validator\Constraints as Assert;
use Vich\UploaderBundle\Mapping\Annotation as Vich;

#[Vich\Uploadable]
#[ORM\Entity(repositoryClass: UserRepository::class)]
#[ORM\Table(name: "user")]
#[ORM\InheritanceType("JOINED")]
#[ORM\DiscriminatorColumn(name: "type", type: "string")]
#[ORM\DiscriminatorMap([
    "user" => User::class,
    "collaborator" => Collaborator::class,
    "manager" => Manager::class,
])]
class User implements UserInterface,PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(name: "iduser", type: "integer")]
    private ?int $idUser = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Name is required.')]
    #[Assert\Length(min: 2, max: 255)]
    private ?string $name = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Email is required.')]
    #[Assert\Email(message: 'The email {{ value }} is not a valid email.', mode: 'strict')]
    #[Assert\Length(max: 255)]
    private ?string $email = null;

    #[ORM\Column(length: 255, nullable: true)]
    #[Assert\PasswordStrength(minScore: 2, message: 'Your password is too weak. Please add numbers, symbols, or mix case.')]
    private ?string $password = null;

    #[ORM\Column(length: 100, nullable: true, unique: true)]
    private ?string $googleId = null;

    #[ORM\Column(length: 100, nullable: true, unique: true)]
    private ?string $linkedinId = null;

    #[ORM\Column(type: 'json', nullable: true)]
    private array $roles = [];

    #[ORM\Column(type: 'boolean', options: ['default' => true])]
    private bool $isEnabled = true;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $resetToken = null;

    #[ORM\Column(nullable: true)]
    private ?\DateTimeImmutable $resetTokenExpiresAt = null;

    #[ORM\Column(length: 255, nullable: true)]
    private ?string $avatarName = null;

    #[Vich\UploadableField(mapping: 'avatars', fileNameProperty: 'avatarName')]
    private ?File $avatarFile = null;

    #[ORM\Column(nullable: true)]
    private ?\DateTimeImmutable $updatedAt = null;

   

    public function getIdUser(): ?int
    {
        return $this->idUser;
    }

    public function setIdUser(int $idUser): static
    {
        $this->idUser = $idUser;

        return $this;
    }

    public function getName(): ?string
    {
        return $this->name;
    }

    public function setName(string $name): static
    {
        $this->name = $name;

        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): static
    {
        $this->email = $email;

        return $this;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(?string $password): static
    {
        $this->password = $password;

        return $this;
    }

    public function getGoogleId(): ?string
    {
        return $this->googleId;
    }

    public function setGoogleId(?string $googleId): static
    {
        $this->googleId = $googleId;

        return $this;
    }

    public function getLinkedinId(): ?string
    {
        return $this->linkedinId;
    }

    public function setLinkedinId(?string $linkedinId): static
    {
        $this->linkedinId = $linkedinId;

        return $this;
    }

    public function getRoles(): array
    {
        // Ensure roles is always an array (handle NULL from database)
        $roles = $this->roles ?? [];
        
        // Guarantee every user at least has ROLE_USER
        $roles[] = 'ROLE_USER';

        return array_unique($roles);
    }

    public function setRoles(array $roles): static
    {
        $this->roles = $roles;

        return $this;
    }

    public function addRole(string $role): static
    {
        if (!in_array($role, $this->roles, true)) {
            $this->roles[] = $role;
        }

        return $this;
    }

    public function removeRole(string $role): static
    {
        $this->roles = array_values(array_diff($this->roles, [$role]));

        return $this;
    }

    public function isEnabled(): bool
    {
        return $this->isEnabled;
    }

    public function setEnabled(bool $isEnabled): static
    {
        $this->isEnabled = $isEnabled;

        return $this;
    }

    public function getResetToken(): ?string
    {
        return $this->resetToken;
    }

    public function setResetToken(?string $resetToken): static
    {
        $this->resetToken = $resetToken;

        return $this;
    }

    public function getResetTokenExpiresAt(): ?\DateTimeImmutable
    {
        return $this->resetTokenExpiresAt;
    }

    public function setResetTokenExpiresAt(?\DateTimeImmutable $resetTokenExpiresAt): static
    {
        $this->resetTokenExpiresAt = $resetTokenExpiresAt;

        return $this;
    }

    public function isResetTokenExpired(): bool
    {
        return $this->resetTokenExpiresAt === null || $this->resetTokenExpiresAt < new \DateTimeImmutable();
    }

    public function getAvatarName(): ?string
    {
        return $this->avatarName;
    }

    public function setAvatarName(?string $avatarName): static
    {
        $this->avatarName = $avatarName;

        return $this;
    }

    public function getAvatarFile(): ?File
    {
        return $this->avatarFile;
    }

    public function setAvatarFile(?File $avatarFile = null): static
    {
        $this->avatarFile = $avatarFile;

        if ($avatarFile !== null) {
            $this->updatedAt = new \DateTimeImmutable();
        }

        return $this;
    }

    public function getUpdatedAt(): ?\DateTimeImmutable
    {
        return $this->updatedAt;
    }

    public function setUpdatedAt(?\DateTimeImmutable $updatedAt): static
    {
        $this->updatedAt = $updatedAt;

        return $this;
    }

    public function eraseCredentials(): void
    {
    }

    public function getUserIdentifier(): string
    {
        return $this->email ?? '';
    }

    public function __serialize(): array
    {
        return [
            'idUser' => $this->idUser,
            'name' => $this->name,
            'email' => $this->email,
            'password' => $this->password,
            'googleId' => $this->googleId,
            'linkedinId' => $this->linkedinId,
            'roles' => $this->roles,
            'isEnabled' => $this->isEnabled,
            'resetToken' => $this->resetToken,
            'resetTokenExpiresAt' => $this->resetTokenExpiresAt,
            'avatarName' => $this->avatarName,
            'updatedAt' => $this->updatedAt,
        ];
    }

    public function __unserialize(array $data): void
    {
        $read = static function (array $payload, string $property) {
            if (array_key_exists($property, $payload)) {
                return $payload[$property];
            }

            $prefixed = [
                "\0*\0{$property}",
                "\0" . self::class . "\0{$property}",
            ];

            foreach ($prefixed as $key) {
                if (array_key_exists($key, $payload)) {
                    return $payload[$key];
                }
            }

            foreach ($payload as $key => $value) {
                if (str_ends_with((string) $key, "\0{$property}")) {
                    return $value;
                }
            }

            return null;
        };

        $this->idUser = $read($data, 'idUser');
        $this->name = $read($data, 'name');
        $this->email = $read($data, 'email');
        $this->password = $read($data, 'password');
        $this->googleId = $read($data, 'googleId');
        $this->linkedinId = $read($data, 'linkedinId');
        $this->roles = $read($data, 'roles') ?? [];
        $this->isEnabled = $read($data, 'isEnabled') ?? true;
        $this->resetToken = $read($data, 'resetToken');
        $this->resetTokenExpiresAt = $read($data, 'resetTokenExpiresAt');
        $this->avatarName = $read($data, 'avatarName');
        $this->updatedAt = $read($data, 'updatedAt');
        $this->avatarFile = null;
    }
}

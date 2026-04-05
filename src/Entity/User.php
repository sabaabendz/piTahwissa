<?php

namespace App\Entity;

use App\Repository\UserRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: UserRepository::class)]
#[ORM\Table(name: 'user')]
#[ORM\HasLifecycleCallbacks]
#[UniqueEntity(fields: ['email'], message: 'This email is already used.')]
class User implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(name: 'id', type: 'integer')]
    private ?int $id = null;

    #[ORM\Column(name: 'email', type: 'string', length: 100, unique: true)]
    #[Assert\NotBlank(message: 'Email is required.')]
    #[Assert\Email(message: 'Please provide a valid email address.')]
    #[Assert\Length(max: 100)]
    private ?string $email = null;

    #[ORM\Column(name: 'password', type: 'string', length: 255)]
    private ?string $password = null;

    #[ORM\Column(name: 'first_name', type: 'string', length: 50, nullable: true)]
    #[Assert\Length(max: 50)]
    private ?string $firstName = null;

    #[ORM\Column(name: 'last_name', type: 'string', length: 50, nullable: true)]
    #[Assert\Length(max: 50)]
    private ?string $lastName = null;

    #[ORM\Column(name: 'phone', type: 'string', length: 20, nullable: true)]
    #[Assert\Length(max: 20)]
    private ?string $phone = null;

    #[ORM\Column(name: 'avatar_url', type: 'string', length: 500, nullable: true)]
    private ?string $avatarUrl = null;

    #[ORM\Column(name: 'description', type: 'text', nullable: true)]
    #[Assert\Length(max: 2000)]
    private ?string $description = null;

    #[ORM\Column(name: 'address', type: 'string', length: 200, nullable: true)]
    #[Assert\Length(max: 200)]
    private ?string $address = null;

    #[ORM\Column(name: 'city', type: 'string', length: 50, nullable: true)]
    #[Assert\Length(max: 50)]
    private ?string $city = null;

    #[ORM\Column(name: 'country', type: 'string', length: 50, nullable: true)]
    #[Assert\Length(max: 50)]
    private ?string $country = null;

    #[ORM\Column(name: 'latitude', type: 'decimal', precision: 10, scale: 8, nullable: true)]
    #[Assert\Range(min: -90, max: 90, notInRangeMessage: 'Latitude must be between {{ min }} and {{ max }}.')]
    private ?string $latitude = null;

    #[ORM\Column(name: 'longitude', type: 'decimal', precision: 11, scale: 8, nullable: true)]
    #[Assert\Range(min: -180, max: 180, notInRangeMessage: 'Longitude must be between {{ min }} and {{ max }}.')]
    private ?string $longitude = null;

    #[ORM\Column(name: 'is_verified', type: 'boolean', options: ['default' => false])]
    private bool $isVerified = false;

    #[ORM\Column(name: 'is_active', type: 'boolean', options: ['default' => true])]
    private bool $isActive = true;

    #[ORM\ManyToOne(targetEntity: Role::class, inversedBy: 'users')]
    #[ORM\JoinColumn(name: 'role_id', referencedColumnName: 'id', nullable: true)]
    #[Assert\NotNull(message: 'Please select a role.')]
    private ?Role $role = null;

    #[ORM\Column(name: 'created_at', type: 'datetime_immutable')]
    private ?\DateTimeImmutable $createdAt = null;

    #[ORM\Column(name: 'updated_at', type: 'datetime_immutable')]
    private ?\DateTimeImmutable $updatedAt = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdUser(): ?int
    {
        return $this->id;
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

    public function setPassword(string $password): static
    {
        $this->password = $password;

        return $this;
    }

    public function getFirstName(): ?string
    {
        return $this->firstName;
    }

    public function setFirstName(?string $firstName): static
    {
        $this->firstName = $firstName;

        return $this;
    }

    public function getLastName(): ?string
    {
        return $this->lastName;
    }

    public function setLastName(?string $lastName): static
    {
        $this->lastName = $lastName;

        return $this;
    }

    public function getPhone(): ?string
    {
        return $this->phone;
    }

    public function setPhone(?string $phone): static
    {
        $this->phone = $phone;

        return $this;
    }

    public function getAvatarUrl(): ?string
    {
        return $this->avatarUrl;
    }

    public function setAvatarUrl(?string $avatarUrl): static
    {
        $this->avatarUrl = $avatarUrl;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(?string $description): static
    {
        $this->description = $description;

        return $this;
    }

    public function getAddress(): ?string
    {
        return $this->address;
    }

    public function setAddress(?string $address): static
    {
        $this->address = $address;

        return $this;
    }

    public function getCity(): ?string
    {
        return $this->city;
    }

    public function setCity(?string $city): static
    {
        $this->city = $city;

        return $this;
    }

    public function getCountry(): ?string
    {
        return $this->country;
    }

    public function setCountry(?string $country): static
    {
        $this->country = $country;

        return $this;
    }

    public function getLatitude(): ?string
    {
        return $this->latitude;
    }

    public function setLatitude(?string $latitude): static
    {
        $this->latitude = $latitude;

        return $this;
    }

    public function getLongitude(): ?string
    {
        return $this->longitude;
    }

    public function setLongitude(?string $longitude): static
    {
        $this->longitude = $longitude;

        return $this;
    }

    public function isVerified(): bool
    {
        return $this->isVerified;
    }

    public function setIsVerified(bool $isVerified): static
    {
        $this->isVerified = $isVerified;

        return $this;
    }

    public function isActive(): bool
    {
        return $this->isActive;
    }

    public function setIsActive(bool $isActive): static
    {
        $this->isActive = $isActive;

        return $this;
    }

    public function isEnabled(): bool
    {
        return $this->isActive;
    }

    public function setEnabled(bool $isEnabled): static
    {
        $this->isActive = $isEnabled;

        return $this;
    }

    public function getRole(): ?Role
    {
        return $this->role;
    }

    public function setRole(?Role $role): static
    {
        $this->role = $role;

        return $this;
    }

    public function getCreatedAt(): ?\DateTimeImmutable
    {
        return $this->createdAt;
    }

    public function setCreatedAt(?\DateTimeImmutable $createdAt): static
    {
        $this->createdAt = $createdAt;

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

    public function getName(): string
    {
        $fullName = trim((string) ($this->firstName . ' ' . $this->lastName));

        return $fullName !== '' ? $fullName : (string) $this->email;
    }

    public function setName(string $name): static
    {
        $parts = preg_split('/\s+/', trim($name), 2);
        $this->firstName = $parts[0] ?? null;
        $this->lastName = $parts[1] ?? null;

        return $this;
    }

    public function getRoles(): array
    {
        $roleName = strtoupper((string) ($this->role?->getName() ?? 'USER'));

        if ($roleName === 'ADMIN') {
            return ['ROLE_ADMIN'];
        }

        if ($roleName === 'AGENT') {
            return ['ROLE_AGENT'];
        }

        return ['ROLE_USER'];
    }

    public function eraseCredentials(): void
    {
    }

    public function getUserIdentifier(): string
    {
        return (string) $this->email;
    }

    #[ORM\PrePersist]
    public function onPrePersist(): void
    {
        $now = new \DateTimeImmutable();
        if ($this->createdAt === null) {
            $this->createdAt = $now;
        }
        $this->updatedAt = $now;
    }

    #[ORM\PreUpdate]
    public function onPreUpdate(): void
    {
        $this->updatedAt = new \DateTimeImmutable();
    }
}

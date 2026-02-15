<?php

namespace App\Entity;

use App\Repository\ManagerRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ManagerRepository::class)]
#[ORM\Table(name: "manager")]
class Manager extends User
{
    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Level is required.')]
    #[Assert\Length(max: 255)]
    private ?string $level = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Department is required.')]
    #[Assert\Length(max: 255)]
    private ?string $department = null;

    #[ORM\Column(length: 20, unique: true)]
    private ?string $enterpriseCode = null;

    

    public function getLevel(): ?string
    {
        return $this->level;
    }

    public function setLevel(string $level): static
    {
        $this->level = $level;

        return $this;
    }

    public function getDepartment(): ?string
    {
        return $this->department;
    }

    public function setDepartment(string $department): static
    {
        $this->department = $department;

        return $this;
    }

    public function getEnterpriseCode(): ?string
    {
        return $this->enterpriseCode;
    }

    public function setEnterpriseCode(string $enterpriseCode): static
    {
        $this->enterpriseCode = $enterpriseCode;

        return $this;
    }

    public function getRoles(): array
    {
        // Get roles from parent (includes JSON roles field + ROLE_USER)
        $roles = parent::getRoles();
        
        // Add ROLE_MANAGER as default for Manager entities
        $roles[] = 'ROLE_MANAGER';
        
        return array_unique($roles);
    }
}

<?php

namespace App\Entity;

use App\Repository\ManagerRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: ManagerRepository::class)]
#[ORM\Table(name: "manager")]

class Manager extends User
{
   
    #[ORM\Column(length: 255)]
    private ?string $levem = null;

    #[ORM\Column(length: 255)]
    private ?string $department = null;

    

    public function getLevem(): ?string
    {
        return $this->levem;
    }

    public function setLevem(string $levem): static
    {
        $this->levem = $levem;

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
}

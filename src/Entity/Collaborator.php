<?php

namespace App\Entity;

use App\Repository\CollaboratorRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CollaboratorRepository::class)]
#[ORM\Table(name: "collaborator")]

class Collaborator extends User
{
   
    #[ORM\Column(length: 255)]
    private ?string $post = null;

    #[ORM\Column(length: 255)]
    private ?string $team = null;

    #[ORM\Column(length: 20)]
    private ?string $enterpriseCode = null;

    

    public function getPost(): ?string
    {
        return $this->post;
    }

    public function setPost(string $post): static
    {
        $this->post = $post;

        return $this;
    }

    public function getTeam(): ?string
    {
        return $this->team;
    }

    public function setTeam(string $team): static
    {
        $this->team = $team;

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
        return ['ROLE_COLLABORATOR'];
    }
}

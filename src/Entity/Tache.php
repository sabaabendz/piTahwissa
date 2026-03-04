<?php

namespace App\Entity;

use App\Repository\TacheRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: TacheRepository::class)]
class Tache
{
    public const PRIORITE_BASSE = 'basse';
    public const PRIORITE_MOYENNE = 'moyenne';
    public const PRIORITE_HAUTE = 'haute';

    public const ETAT_A_FAIRE = 'a_faire';
    public const ETAT_EN_COURS = 'en_cours';
    public const ETAT_TERMINE = 'termine';

    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private int $id = 0;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Le libellé est requis.')]
    #[Assert\Length(max: 255)]
    private ?string $libelle = null;

    #[ORM\Column(length: 50)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: [self::PRIORITE_BASSE, self::PRIORITE_MOYENNE, self::PRIORITE_HAUTE])]
    private string $priorite = self::PRIORITE_MOYENNE;

    #[ORM\Column(type: Types::DATE_MUTABLE)]
    #[Assert\NotBlank(message: 'La date limite est requise.')]
    private ?\DateTimeInterface $dateLimite = null;

    #[ORM\Column(length: 50)]
    #[Assert\NotBlank]
    #[Assert\Choice(choices: [self::ETAT_A_FAIRE, self::ETAT_EN_COURS, self::ETAT_TERMINE])]
    private string $etat = self::ETAT_A_FAIRE;

    #[ORM\ManyToOne(targetEntity: Projet::class, inversedBy: 'taches')]
    #[ORM\JoinColumn(nullable: false, onDelete: 'CASCADE')]
    #[Assert\NotBlank(message: 'Le projet est requis.')]
    private ?Projet $projet = null;

    public function getId(): ?int
    {
        return $this->id > 0 ? $this->id : null;
    }

    public function getLibelle(): ?string
    {
        return $this->libelle;
    }

    public function setLibelle(string $libelle): static
    {
        $this->libelle = $libelle;
        return $this;
    }

    public function getPriorite(): string
    {
        return $this->priorite;
    }

    public function setPriorite(string $priorite): static
    {
        $this->priorite = $priorite;
        return $this;
    }

    public function getDateLimite(): ?\DateTimeInterface
    {
        return $this->dateLimite;
    }

    public function setDateLimite(?\DateTimeInterface $dateLimite): static
    {
        $this->dateLimite = $dateLimite;
        return $this;
    }

    public function getEtat(): string
    {
        return $this->etat;
    }

    public function setEtat(string $etat): static
    {
        $this->etat = $etat;
        return $this;
    }

    public function getProjet(): ?Projet
    {
        return $this->projet;
    }

    public function setProjet(?Projet $projet): static
    {
        $this->projet = $projet;
        return $this;
    }
}

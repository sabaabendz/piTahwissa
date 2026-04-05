<?php

namespace App\Entity;

use App\Repository\ReservationVoyageRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ReservationVoyageRepository::class)]
#[ORM\Table(name: 'reservationvoyage')]
class ReservationVoyage
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: Types::INTEGER)]
    private ?int $id = null;

    #[ORM\Column(name: 'idUtilisateur', type: Types::INTEGER)]
    #[Assert\NotBlank(message: 'L\'identifiant utilisateur est obligatoire')]
    #[Assert\Positive(message: 'L\'identifiant utilisateur doit être positif')]
    private ?int $idUtilisateur = null;

    #[ORM\ManyToOne(targetEntity: Voyage::class, inversedBy: 'reservations')]
    #[ORM\JoinColumn(name: 'id_voyage', referencedColumnName: 'id', nullable: false)]
    #[Assert\NotNull(message: 'Le voyage est obligatoire')]
    private ?Voyage $voyage = null;

    #[ORM\Column(name: 'date_reservation', type: Types::DATETIME_MUTABLE, nullable: true)]
    #[Assert\NotBlank(message: 'La date de réservation est obligatoire')]
    private ?\DateTimeInterface $dateReservation = null;

    #[ORM\Column(type: Types::STRING, length: 20, enumType: null)]
    #[Assert\NotBlank(message: 'Le statut est obligatoire')]
    #[Assert\Choice(
        choices: ['EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE'],
        message: 'Le statut doit être EN_ATTENTE, CONFIRMEE, ANNULEE ou TERMINEE'
    )]
    private ?string $statut = 'EN_ATTENTE';

    #[ORM\Column(name: 'nbrPersonnes', type: Types::INTEGER)]
    #[Assert\NotBlank(message: 'Le nombre de personnes est obligatoire')]
    #[Assert\Positive(message: 'Le nombre de personnes doit être au moins 1')]
    #[Assert\LessThanOrEqual(value: 50, message: 'Le nombre de personnes ne peut pas dépasser 50')]
    private ?int $nbrPersonnes = 1;

    #[ORM\Column(name: 'montantTotal', type: Types::DECIMAL, precision: 10, scale: 2)]
    private ?string $montantTotal = '0.00';

    #[ORM\Column(name: 'dateCreation', type: Types::DATETIME_MUTABLE)]
    private ?\DateTimeInterface $dateCreation = null;

    public function __construct()
    {
        $this->dateCreation = new \DateTime();
        $this->dateReservation = new \DateTime();
        $this->statut = 'EN_ATTENTE';
        $this->nbrPersonnes = 1;
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdUtilisateur(): ?int
    {
        return $this->idUtilisateur;
    }

    public function setIdUtilisateur(int $idUtilisateur): static
    {
        $this->idUtilisateur = $idUtilisateur;
        return $this;
    }

    public function getVoyage(): ?Voyage
    {
        return $this->voyage;
    }

    public function setVoyage(?Voyage $voyage): static
    {
        $this->voyage = $voyage;
        return $this;
    }

    public function getDateReservation(): ?\DateTimeInterface
    {
        return $this->dateReservation;
    }

    public function setDateReservation(?\DateTimeInterface $dateReservation): static
    {
        $this->dateReservation = $dateReservation;
        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;
        return $this;
    }

    public function getNbrPersonnes(): ?int
    {
        return $this->nbrPersonnes;
    }

    public function setNbrPersonnes(int $nbrPersonnes): static
    {
        $this->nbrPersonnes = $nbrPersonnes;
        return $this;
    }

    public function getMontantTotal(): ?string
    {
        return $this->montantTotal;
    }

    public function setMontantTotal(string $montantTotal): static
    {
        $this->montantTotal = $montantTotal;
        return $this;
    }

    public function getDateCreation(): ?\DateTimeInterface
    {
        return $this->dateCreation;
    }

    public function setDateCreation(\DateTimeInterface $dateCreation): static
    {
        $this->dateCreation = $dateCreation;
        return $this;
    }

    /**
     * Calculates and sets the total amount based on voyage price and number of persons
     */
    public function calculerMontantTotal(): static
    {
        if ($this->voyage && $this->nbrPersonnes) {
            $prix = (float) $this->voyage->getPrixUnitaire();
            $this->montantTotal = number_format($prix * $this->nbrPersonnes, 2, '.', '');
        }
        return $this;
    }

    public function getStatutLabel(): string
    {
        return match ($this->statut) {
            'EN_ATTENTE' => 'En attente',
            'CONFIRMEE' => 'Confirmée',
            'ANNULEE' => 'Annulée',
            'TERMINEE' => 'Terminée',
            default => $this->statut,
        };
    }

    public function getStatutBadgeClass(): string
    {
        return match ($this->statut) {
            'EN_ATTENTE' => 'warning',
            'CONFIRMEE' => 'success',
            'ANNULEE' => 'danger',
            'TERMINEE' => 'info',
            default => 'secondary',
        };
    }
}

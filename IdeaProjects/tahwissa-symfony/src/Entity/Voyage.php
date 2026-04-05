<?php

namespace App\Entity;

use App\Repository\VoyageRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: VoyageRepository::class)]
#[ORM\Table(name: 'voyage')]
class Voyage
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column(type: Types::INTEGER)]
    private ?int $id = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'Le titre est obligatoire')]
    #[Assert\Length(
        min: 3,
        max: 255,
        minMessage: 'Le titre doit contenir au moins {{ limit }} caractères',
        maxMessage: 'Le titre ne peut pas dépasser {{ limit }} caractères'
    )]
    private ?string $titre = null;

    #[ORM\Column(type: Types::TEXT, nullable: true)]
    private ?string $description = null;

    #[ORM\Column(length: 255)]
    #[Assert\NotBlank(message: 'La destination est obligatoire')]
    private ?string $destination = null;

    #[ORM\Column(length: 100, nullable: true)]
    private ?string $categorie = null;

    #[ORM\Column(name: 'prix_unitaire', type: Types::DECIMAL, precision: 10, scale: 2)]
    #[Assert\NotBlank(message: 'Le prix est obligatoire')]
    #[Assert\Positive(message: 'Le prix doit être positif')]
    private ?string $prixUnitaire = null;

    #[ORM\Column(name: 'date_depart', type: Types::DATE_MUTABLE, nullable: true)]
    #[Assert\NotBlank(message: 'La date de départ est obligatoire')]
    #[Assert\GreaterThanOrEqual(
        'today',
        message: 'La date de départ doit être aujourd\'hui ou dans le futur'
    )]
    private ?\DateTimeInterface $dateDepart = null;

    #[ORM\Column(name: 'date_retour', type: Types::DATE_MUTABLE, nullable: true)]
    #[Assert\NotBlank(message: 'La date de retour est obligatoire')]
    #[Assert\GreaterThan(
        propertyPath: 'dateDepart',
        message: 'La date de retour doit être après la date de départ'
    )]
    private ?\DateTimeInterface $dateRetour = null;

    #[ORM\Column(name: 'places_disponibles', type: Types::INTEGER)]
    #[Assert\NotBlank(message: 'Le nombre de places est obligatoire')]
    #[Assert\Positive(message: 'Le nombre de places doit être positif')]
    private ?int $placesDisponibles = null;

    #[ORM\Column(name: 'image_url', length: 255, nullable: true)]
    private ?string $imageUrl = null;

    #[ORM\Column(length: 20, nullable: true, options: ['default' => 'ACTIF'])]
    private ?string $statut = 'ACTIF';

    #[ORM\Column(name: 'created_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $createdAt = null;

    #[ORM\Column(name: 'updated_at', type: Types::DATETIME_MUTABLE, nullable: true)]
    private ?\DateTimeInterface $updatedAt = null;

    #[ORM\OneToMany(mappedBy: 'voyage', targetEntity: ReservationVoyage::class)]
    private Collection $reservations;

    public function __construct()
    {
        $this->reservations = new ArrayCollection();
        $this->createdAt = new \DateTime();
        $this->updatedAt = new \DateTime();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getTitre(): ?string
    {
        return $this->titre;
    }

    public function setTitre(string $titre): static
    {
        $this->titre = $titre;
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

    public function getDestination(): ?string
    {
        return $this->destination;
    }

    public function setDestination(string $destination): static
    {
        $this->destination = $destination;
        return $this;
    }

    public function getCategorie(): ?string
    {
        return $this->categorie;
    }

    public function setCategorie(?string $categorie): static
    {
        $this->categorie = $categorie;
        return $this;
    }

    public function getPrixUnitaire(): ?string
    {
        return $this->prixUnitaire;
    }

    public function setPrixUnitaire(string $prixUnitaire): static
    {
        $this->prixUnitaire = $prixUnitaire;
        return $this;
    }

    public function getDateDepart(): ?\DateTimeInterface
    {
        return $this->dateDepart;
    }

    public function setDateDepart(?\DateTimeInterface $dateDepart): static
    {
        $this->dateDepart = $dateDepart;
        return $this;
    }

    public function getDateRetour(): ?\DateTimeInterface
    {
        return $this->dateRetour;
    }

    public function setDateRetour(?\DateTimeInterface $dateRetour): static
    {
        $this->dateRetour = $dateRetour;
        return $this;
    }

    public function getPlacesDisponibles(): ?int
    {
        return $this->placesDisponibles;
    }

    public function setPlacesDisponibles(int $placesDisponibles): static
    {
        $this->placesDisponibles = $placesDisponibles;
        return $this;
    }

    public function getImageUrl(): ?string
    {
        return $this->imageUrl;
    }

    public function setImageUrl(?string $imageUrl): static
    {
        $this->imageUrl = $imageUrl;
        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(?string $statut): static
    {
        $this->statut = $statut;
        return $this;
    }

    public function getCreatedAt(): ?\DateTimeInterface
    {
        return $this->createdAt;
    }

    public function setCreatedAt(?\DateTimeInterface $createdAt): static
    {
        $this->createdAt = $createdAt;
        return $this;
    }

    public function getUpdatedAt(): ?\DateTimeInterface
    {
        return $this->updatedAt;
    }

    public function setUpdatedAt(?\DateTimeInterface $updatedAt): static
    {
        $this->updatedAt = $updatedAt;
        return $this;
    }

    /**
     * @return Collection<int, ReservationVoyage>
     */
    public function getReservations(): Collection
    {
        return $this->reservations;
    }

    public function addReservation(ReservationVoyage $reservation): static
    {
        if (!$this->reservations->contains($reservation)) {
            $this->reservations->add($reservation);
            $reservation->setVoyage($this);
        }
        return $this;
    }

    public function removeReservation(ReservationVoyage $reservation): static
    {
        if ($this->reservations->removeElement($reservation)) {
            if ($reservation->getVoyage() === $this) {
                $reservation->setVoyage(null);
            }
        }
        return $this;
    }

    public function __toString(): string
    {
        return $this->titre . ' - ' . $this->destination . ' (' . $this->prixUnitaire . ' TND)';
    }

    /**
     * Get duration in days
     */
    public function getDuree(): ?int
    {
        if ($this->dateDepart && $this->dateRetour) {
            return $this->dateDepart->diff($this->dateRetour)->days;
        }
        return null;
    }

    public function isActif(): bool
    {
        return $this->statut === 'ACTIF';
    }
}

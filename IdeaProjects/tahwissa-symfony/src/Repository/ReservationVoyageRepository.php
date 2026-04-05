<?php

namespace App\Repository;

use App\Entity\ReservationVoyage;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<ReservationVoyage>
 */
class ReservationVoyageRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, ReservationVoyage::class);
    }

    /**
     * Find all reservations with joined voyage data, ordered by newest
     */
    public function findAllWithVoyage(): array
    {
        return $this->createQueryBuilder('r')
            ->leftJoin('r.voyage', 'v')
            ->addSelect('v')
            ->orderBy('r.dateCreation', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Find reservations for a specific user
     */
    public function findByUser(int $userId): array
    {
        return $this->createQueryBuilder('r')
            ->leftJoin('r.voyage', 'v')
            ->addSelect('v')
            ->andWhere('r.idUtilisateur = :uid')
            ->setParameter('uid', $userId)
            ->orderBy('r.dateCreation', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Find reservations by status
     */
    public function findByStatut(string $statut): array
    {
        return $this->createQueryBuilder('r')
            ->leftJoin('r.voyage', 'v')
            ->addSelect('v')
            ->andWhere('r.statut = :statut')
            ->setParameter('statut', $statut)
            ->orderBy('r.dateCreation', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Search reservations
     */
    public function search(string $keyword, ?string $statut = null): array
    {
        $qb = $this->createQueryBuilder('r')
            ->leftJoin('r.voyage', 'v')
            ->addSelect('v');

        if ($keyword) {
            $qb->andWhere('v.titre LIKE :kw OR v.destination LIKE :kw OR CAST(r.id AS string) LIKE :kw')
               ->setParameter('kw', '%' . $keyword . '%');
        }

        if ($statut) {
            $qb->andWhere('r.statut = :statut')
               ->setParameter('statut', $statut);
        }

        return $qb->orderBy('r.dateCreation', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Get statistics for dashboard
     */
    public function getStats(): array
    {
        $total = $this->count([]);
        $confirmees = $this->count(['statut' => 'CONFIRMEE']);
        $enAttente = $this->count(['statut' => 'EN_ATTENTE']);
        $annulees = $this->count(['statut' => 'ANNULEE']);

        $revenu = $this->createQueryBuilder('r')
            ->select('COALESCE(SUM(r.montantTotal), 0) as revenu')
            ->andWhere('r.statut = :statut')
            ->setParameter('statut', 'CONFIRMEE')
            ->getQuery()
            ->getSingleScalarResult();

        return [
            'total' => $total,
            'confirmees' => $confirmees,
            'enAttente' => $enAttente,
            'annulees' => $annulees,
            'revenu' => (float) $revenu,
        ];
    }

    public function save(ReservationVoyage $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);
        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(ReservationVoyage $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);
        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}

<?php

namespace App\Repository;

use App\Entity\Voyage;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Voyage>
 */
class VoyageRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Voyage::class);
    }

    /**
     * Find active voyages
     */
    public function findActifs(): array
    {
        return $this->createQueryBuilder('v')
            ->andWhere('v.statut = :statut')
            ->setParameter('statut', 'ACTIF')
            ->orderBy('v.dateDepart', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Search voyages by keyword
     */
    public function search(string $keyword): array
    {
        return $this->createQueryBuilder('v')
            ->andWhere('v.titre LIKE :kw OR v.destination LIKE :kw OR v.categorie LIKE :kw')
            ->setParameter('kw', '%' . $keyword . '%')
            ->orderBy('v.createdAt', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Find voyages with available places
     */
    public function findAvailable(): array
    {
        return $this->createQueryBuilder('v')
            ->andWhere('v.statut = :statut')
            ->andWhere('v.placesDisponibles > 0')
            ->andWhere('v.dateDepart >= :today')
            ->setParameter('statut', 'ACTIF')
            ->setParameter('today', new \DateTime('today'))
            ->orderBy('v.dateDepart', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Get stats for dashboard
     */
    public function getStats(): array
    {
        $total = $this->count([]);
        $actifs = $this->count(['statut' => 'ACTIF']);

        $placesResult = $this->createQueryBuilder('v')
            ->select('SUM(v.placesDisponibles) as totalPlaces')
            ->andWhere('v.statut = :statut')
            ->setParameter('statut', 'ACTIF')
            ->getQuery()
            ->getSingleScalarResult();

        $prixMoyen = $this->createQueryBuilder('v')
            ->select('AVG(v.prixUnitaire) as prixMoyen')
            ->getQuery()
            ->getSingleScalarResult();

        return [
            'total' => $total,
            'actifs' => $actifs,
            'places' => (int) ($placesResult ?? 0),
            'prixMoyen' => round((float) ($prixMoyen ?? 0), 2),
        ];
    }

    public function save(Voyage $entity, bool $flush = false): void
    {
        $this->getEntityManager()->persist($entity);
        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }

    public function remove(Voyage $entity, bool $flush = false): void
    {
        $this->getEntityManager()->remove($entity);
        if ($flush) {
            $this->getEntityManager()->flush();
        }
    }
}

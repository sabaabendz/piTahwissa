<?php

namespace App\Repository;

use App\Entity\Manager;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Manager>
 */
class ManagerRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Manager::class);
    }

    //    /**
    //     * @return Manager[] Returns an array of Manager objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('m')
    //            ->andWhere('m.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('m.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    public function searchByTerm(string $term): array
    {
        return $this->createQueryBuilder('m')
            ->andWhere('m.name LIKE :term OR m.email LIKE :term OR m.department LIKE :term OR m.level LIKE :term')
            ->setParameter('term', '%' . $term . '%')
            ->orderBy('m.name', 'ASC')
            ->getQuery()
            ->getResult();
    }
}

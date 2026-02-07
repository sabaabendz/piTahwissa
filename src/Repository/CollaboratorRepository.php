<?php

namespace App\Repository;

use App\Entity\Collaborator;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Collaborator>
 */
class CollaboratorRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Collaborator::class);
    }

    /**
     * Find all collaborators by enterprise code (tenant isolation)
     * 
     * @param string $enterpriseCode
     * @return Collaborator[]
     */
    public function findByEnterpriseCode(string $enterpriseCode): array
    {
        return $this->createQueryBuilder('c')
            ->andWhere('c.enterpriseCode = :enterpriseCode')
            ->setParameter('enterpriseCode', $enterpriseCode)
            ->orderBy('c.idUser', 'ASC')
            ->getQuery()
            ->getResult();
    }
}

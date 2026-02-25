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

    /**
     * Search collaborators by enterprise code and keyword.
     *
     * @param string $enterpriseCode
     * @param string $query
     * @return Collaborator[]
     */
    public function searchByEnterpriseCode(string $enterpriseCode, string $query): array
    {
        $trimmedQuery = trim($query);

        if ($trimmedQuery == '') {
            return $this->findByEnterpriseCode($enterpriseCode);
        }

        return $this->createQueryBuilder('c')
            ->andWhere('c.enterpriseCode = :enterpriseCode')
            ->andWhere('LOWER(c.name) LIKE :query OR LOWER(c.email) LIKE :query OR LOWER(c.post) LIKE :query OR LOWER(c.team) LIKE :query')
            ->setParameter('enterpriseCode', $enterpriseCode)
            ->setParameter('query', '%' . mb_strtolower($trimmedQuery) . '%')
            ->orderBy('c.idUser', 'ASC')
            ->getQuery()
            ->getResult();
    }
}

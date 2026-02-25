<?php

namespace App\Repository;

use App\Entity\Collaborator;
use App\Entity\Manager;
use App\Entity\User;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<User>
 */
class UserRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, User::class);
    }

    //    /**
    //     * @return User[] Returns an array of User objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('u')
    //            ->andWhere('u.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('u.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    public function searchByTerm(string $term): array
    {
        return $this->createQueryBuilder('u')
            ->andWhere('u.name LIKE :term OR u.email LIKE :term')
            ->setParameter('term', '%' . $term . '%')
            ->orderBy('u.name', 'ASC')
            ->getQuery()
            ->getResult();
    }

    /**
     * @param string|null $role all|admin|manager|collaborator
     */
    public function findByRoleAndSearch(?string $role = 'all', ?string $term = null): array
    {
        $normalizedRole = $role ?? 'all';

        $qb = $this->createQueryBuilder('u')
            ->orderBy('u.name', 'ASC');

        if ($term !== null && trim($term) !== '') {
            $qb
                ->andWhere('u.name LIKE :term OR u.email LIKE :term')
                ->setParameter('term', '%' . trim($term) . '%');
        }

        if ($normalizedRole === 'manager') {
            $qb->andWhere('u INSTANCE OF ' . Manager::class);
        } elseif ($normalizedRole === 'collaborator') {
            $qb->andWhere('u INSTANCE OF ' . Collaborator::class);
        } elseif ($normalizedRole === 'admin') {
            $qb->andWhere('u.roles LIKE :adminRole')
                ->setParameter('adminRole', '%ROLE_ADMIN%');
        }

        return $qb->getQuery()->getResult();
    }
}

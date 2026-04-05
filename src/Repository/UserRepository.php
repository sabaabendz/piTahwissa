<?php

namespace App\Repository;

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

    public function findByEmail(string $email): ?User
    {
        return $this->createQueryBuilder('u')
            ->andWhere('u.email = :email')
            ->setParameter('email', $email)
            ->getQuery()
            ->getOneOrNullResult();
    }

    /** @return User[] */
    public function findActiveUsers(): array
    {
        return $this->createQueryBuilder('u')
            ->andWhere('u.isActive = :active')
            ->setParameter('active', true)
            ->orderBy('u.createdAt', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * @return User[]
     */
    public function findByRole(string $roleName): array
    {
        $qb = $this->createQueryBuilder('u')
            ->leftJoin('u.role', 'r')
            ->andWhere('r.name = :roleName')
            ->setParameter('roleName', strtoupper($roleName))
            ->orderBy('u.createdAt', 'DESC');

        return $qb->getQuery()->getResult();
    }

    /** @return User[] */
    public function findByRoleAndSearch(?string $role = 'all', ?string $term = null): array
    {
        $qb = $this->createQueryBuilder('u')
            ->leftJoin('u.role', 'r')
            ->addSelect('r')
            ->orderBy('u.createdAt', 'DESC');

        if ($term !== null && trim($term) !== '') {
            $termLike = '%' . trim($term) . '%';
            $qb->andWhere('u.email LIKE :term OR u.firstName LIKE :term OR u.lastName LIKE :term')
                ->setParameter('term', $termLike);
        }

        if ($role !== null && strtolower($role) !== 'all') {
            $normalized = strtoupper($role);

            $qb->andWhere('r.name = :role')
                ->setParameter('role', $normalized);
        }

        return $qb->getQuery()->getResult();
    }
}

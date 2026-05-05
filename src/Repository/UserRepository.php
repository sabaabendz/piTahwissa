<?php

namespace App\Repository;

use App\Entity\User;
use DateTimeImmutable;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Security\Core\Exception\UnsupportedUserException;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\PasswordUpgraderInterface;

/**
 * @extends ServiceEntityRepository<User>
 */
class UserRepository extends ServiceEntityRepository implements PasswordUpgraderInterface
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, User::class);
    }

    public function upgradePassword(PasswordAuthenticatedUserInterface $user, string $newHashedPassword): void
    {
        if (!$user instanceof User) {
            throw new UnsupportedUserException(sprintf('Instances of "%s" are not supported.', $user::class));
        }

        $user->setPassword($newHashedPassword);
        $this->getEntityManager()->persist($user);
        $this->getEntityManager()->flush();
    }

    public function findByEmail(string $email): ?User
    {
        return $this->createQueryBuilder('u')
            ->andWhere('u.email = :email')
            ->setParameter('email', $email)
            ->getQuery()
            ->getOneOrNullResult();
    }

    /**
     * @return array<int, User>
     */
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
     * @return array<int, User>
     */
    public function findByRole(string $roleName): array
    {
        return $this->createQueryBuilder('u')
            ->join('u.role', 'r')
            ->andWhere('r.name = :roleName')
            ->setParameter('roleName', $roleName)
            ->orderBy('u.createdAt', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * @return array<int, User>
     */
    public function findByRoleAndSearch(string $roleName, ?string $searchQuery = null): array
    {
        $qb = $this->createQueryBuilder('u')
            ->join('u.role', 'r')
            ->andWhere('r.name = :roleName')
            ->setParameter('roleName', $roleName);

        if ($searchQuery) {
            $qb->andWhere(
                $qb->expr()->orX(
                    $qb->expr()->like('u.firstName', ':search'),
                    $qb->expr()->like('u.lastName', ':search'),
                    $qb->expr()->like('u.email', ':search')
                )
            )
            ->setParameter('search', '%' . $searchQuery . '%');
        }

        return $qb->orderBy('u.createdAt', 'DESC')
            ->getQuery()
            ->getResult();
    }

    /**
     * Return all users indexed by their ID.
     * @return array<int, User>
     */
    public function findAllIndexedById(): array
    {
        $users = $this->findAll();
        $indexed = [];
        foreach ($users as $user) {
            $id = $user->getId();
            if ($id === null) {
                continue;
            }
            $indexed[$id] = $user;
        }
        return $indexed;
    }

    /**
     * @return array{total:int,active:int,inactive:int,byRole:array{USER:int,AGENT:int,ADMIN:int,UNASSIGNED:int}}
     */
    public function getGlobalStatistics(): array
    {
        $total = (int) $this->createQueryBuilder('u')
            ->select('COUNT(u.id)')
            ->getQuery()
            ->getSingleScalarResult();

        $active = (int) $this->createQueryBuilder('u')
            ->select('COUNT(u.id)')
            ->andWhere('u.isActive = :active')
            ->setParameter('active', true)
            ->getQuery()
            ->getSingleScalarResult();

        return [
            'total' => $total,
            'active' => $active,
            'inactive' => max(0, $total - $active),
            'byRole' => $this->getCountsByRole(),
        ];
    }

    /**
     * @return array{USER:int,AGENT:int,ADMIN:int,UNASSIGNED:int}
     */
    public function getCountsByRole(): array
    {
        $distribution = [
            'USER' => 0,
            'AGENT' => 0,
            'ADMIN' => 0,
            'UNASSIGNED' => 0,
        ];

        $rows = $this->createQueryBuilder('u')
            ->select('COALESCE(r.name, :unassigned) AS roleName, COUNT(u.id) AS total')
            ->leftJoin('u.role', 'r')
            ->groupBy('roleName')
            ->setParameter('unassigned', 'UNASSIGNED')
            ->getQuery()
            ->getArrayResult();

        foreach ($rows as $row) {
            $roleName = strtoupper((string) ($row['roleName'] ?? 'UNASSIGNED'));
            if (!array_key_exists($roleName, $distribution)) {
                $roleName = 'UNASSIGNED';
            }
            $distribution[$roleName] = (int) ($row['total'] ?? 0);
        }

        return $distribution;
    }

    /**
     * @return array<int, User>
     */
    public function findLatestRegisteredUsers(int $limit = 5): array
    {
        return $this->createQueryBuilder('u')
            ->orderBy('u.createdAt', 'DESC')
            ->setMaxResults($limit)
            ->getQuery()
            ->getResult();
    }

    public function countInactiveForMoreThanDays(int $days = 30): int
    {
        $threshold = new DateTimeImmutable(sprintf('-%d days', $days));

        return (int) $this->createQueryBuilder('u')
            ->select('COUNT(u.id)')
            ->andWhere('u.isActive = :inactive')
            ->andWhere('u.updatedAt <= :threshold')
            ->setParameter('inactive', false)
            ->setParameter('threshold', $threshold)
            ->getQuery()
            ->getSingleScalarResult();
    }
}

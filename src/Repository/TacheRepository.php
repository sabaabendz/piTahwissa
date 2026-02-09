<?php

namespace App\Repository;

use App\Entity\Tache;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Tache>
 */
class TacheRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Tache::class);
    }

    public function searchAndSort(?string $search, ?int $projetId = null, ?string $sortBy = 'id', ?string $order = 'ASC'): array
    {
        $qb = $this->createQueryBuilder('t')
            ->leftJoin('t.projet', 'p')
            ->addSelect('p');
        if ($search !== null && $search !== '') {
            $qb->andWhere('t.libelle LIKE :search OR p.nom LIKE :search')
                ->setParameter('search', '%' . $search . '%');
        }
        if ($projetId !== null) {
            $qb->andWhere('t.projet = :pid')->setParameter('pid', $projetId);
        }
        $allowedSort = ['id', 'libelle', 'priorite', 'dateLimite', 'etat'];
        if (!in_array($sortBy, $allowedSort, true)) {
            $sortBy = 'id';
        }
        $qb->orderBy('t.' . $sortBy, $order === 'DESC' ? 'DESC' : 'ASC');
        return $qb->getQuery()->getResult();
    }
}

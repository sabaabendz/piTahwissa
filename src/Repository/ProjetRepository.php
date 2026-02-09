<?php

namespace App\Repository;

use App\Entity\Projet;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Projet>
 */
class ProjetRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Projet::class);
    }

    public function searchAndSort(?string $search, ?string $sortBy = 'id', ?string $order = 'ASC'): array
    {
        $qb = $this->createQueryBuilder('p');
        if ($search !== null && $search !== '') {
            $qb->andWhere('p.nom LIKE :search OR p.description LIKE :search')
                ->setParameter('search', '%' . $search . '%');
        }
        $allowedSort = ['id', 'nom', 'dateDebut', 'dateEcheance', 'statut'];
        if (!in_array($sortBy, $allowedSort, true)) {
            $sortBy = 'id';
        }
        $qb->orderBy('p.' . $sortBy, $order === 'DESC' ? 'DESC' : 'ASC');
        return $qb->getQuery()->getResult();
    }
}

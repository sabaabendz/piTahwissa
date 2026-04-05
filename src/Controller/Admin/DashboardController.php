<?php

namespace App\Controller\Admin;

use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/admin')]
class DashboardController extends AbstractController
{
    #[Route('/', name: 'app_admin_dashboard', methods: ['GET'])]
    public function index(
        UserRepository $userRepository,
        EntityManagerInterface $entityManager
    ): Response
    {
        $users = $userRepository->findBy([], ['id' => 'DESC'], 8);
        $totalUsers = $userRepository->count([]);
        $totalAgents = $entityManager->createQuery('SELECT COUNT(u.id) FROM App\\Entity\\User u JOIN u.role r WHERE r.name = :name')
            ->setParameter('name', 'AGENT')
            ->getSingleScalarResult();
        $totalRegularUsers = $entityManager->createQuery('SELECT COUNT(u.id) FROM App\\Entity\\User u JOIN u.role r WHERE r.name = :name')
            ->setParameter('name', 'USER')
            ->getSingleScalarResult();
        
        return $this->render('admin/dashboard.html.twig', [
            'users' => $users,
            'totalUsers' => $totalUsers,
            'totalAgents' => $totalAgents,
            'totalRegularUsers' => $totalRegularUsers,
        ]);
    }
}

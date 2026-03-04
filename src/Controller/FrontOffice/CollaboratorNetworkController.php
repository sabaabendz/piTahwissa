<?php

namespace App\Controller\FrontOffice;

use App\Entity\Collaborator;
use App\Entity\Manager;
use App\Entity\User;
use App\Repository\CollaboratorRepository;
use App\Repository\ManagerRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[IsGranted('ROLE_PROJECT_ACCESS')]
class CollaboratorNetworkController extends AbstractController
{
    #[Route('/collaborator/network', name: 'app_collaborator_network', methods: ['GET'])]
    public function network(
        ManagerRepository $managerRepository,
        CollaboratorRepository $collaboratorRepository
    ): Response {
        $user = $this->getUser();

        if (!$user instanceof User) {
            throw $this->createAccessDeniedException('You must be logged in to access this page.');
        }

        // Get enterprise code - works for both Manager and Collaborator entities
        // as they both have getEnterpriseCode() method.
        if (!method_exists($user, 'getEnterpriseCode')) {
             throw $this->createAccessDeniedException('Only managers and collaborators can access this page.');
        }
        
        $enterpriseCode = $user->getEnterpriseCode();
        
        if (!$enterpriseCode) {
            throw $this->createAccessDeniedException('No enterprise code found for your account.');
        }

        // Find the manager with the same enterprise code
        $manager = $managerRepository->findOneBy(['enterpriseCode' => $enterpriseCode]);
        
        // Find all collaborators with the same enterprise code (excluding current user if collaborator)
        $userId = $user->getIdUser();
        $collaborators = $collaboratorRepository->createQueryBuilder('c')
            ->where('c.enterpriseCode = :code')
            ->andWhere('c.idUser != :userId')
            ->setParameter('code', $enterpriseCode)
            ->setParameter('userId', $userId)
            ->orderBy('c.name', 'ASC')
            ->getQuery()
            ->getResult();

        return $this->render('frontoffice/collaborator/network.html.twig', [
            'manager' => $manager,
            'collaborators' => $collaborators,
            'enterpriseCode' => $enterpriseCode,
        ]);
    }
}

<?php

namespace App\Controller\FrontOffice;

use App\Repository\CollaboratorRepository;
use App\Repository\ManagerRepository;
use App\Repository\UserRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class UserFrontController extends AbstractController
{
    #[Route('/users', name: 'app_front_users', methods: ['GET'])]
    public function index(Request $request, UserRepository $userRepository): Response
    {
        $query = $request->query->get('q');
        if ($query) {
            $users = $userRepository->searchByTerm($query);
        } else {
            $users = $userRepository->findAll();
        }

        return $this->render('frontoffice/users/index.html.twig', [
            'users' => $users,
        ]);
    }

    #[Route('/users/managers', name: 'app_front_managers', methods: ['GET'])]
    public function managers(Request $request, ManagerRepository $managerRepository): Response
    {
        $query = $request->query->get('q');
        if ($query) {
            $managers = $managerRepository->searchByTerm($query);
        } else {
            $managers = $managerRepository->findAll();
        }

        return $this->render('frontoffice/users/managers.html.twig', [
            'managers' => $managers,
        ]);
    }

    #[Route('/users/collaborators', name: 'app_front_collaborators', methods: ['GET'])]
    public function collaborators(Request $request, CollaboratorRepository $collaboratorRepository): Response
    {
        $query = $request->query->get('q');
        if ($query) {
            $collaborators = $collaboratorRepository->searchByTerm($query);
        } else {
            $collaborators = $collaboratorRepository->findAll();
        }

        return $this->render('frontoffice/users/collaborators.html.twig', [
            'collaborators' => $collaborators,
        ]);
    }

    #[Route('/users/search', name: 'app_front_users_search', methods: ['GET'])]
    public function search(
        Request $request, 
        UserRepository $userRepository, 
        ManagerRepository $managerRepository, 
        CollaboratorRepository $collaboratorRepository
    ): Response {
        $query = $request->query->get('q', '');
        $type = $request->query->get('type', 'all');

        $data = [];
        if ($type === 'manager') {
            $data['managers'] = $managerRepository->searchByTerm($query);
        } elseif ($type === 'collaborator') {
            $data['collaborators'] = $collaboratorRepository->searchByTerm($query);
        } else {
            $data['users'] = $userRepository->searchByTerm($query);
        }

        return $this->render('frontoffice/users/_list.html.twig', $data);
    }
}

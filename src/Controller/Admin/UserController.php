<?php

namespace App\Controller\Admin;

use App\Entity\User;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[Route('/admin/users')]
#[IsGranted('ROLE_ADMIN')]
class UserController extends AbstractController
{
    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly EntityManagerInterface $entityManager
    ) {
    }

    #[Route('', name: 'app_admin_users_index', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $role = strtolower((string) $request->query->get('role', 'all'));
        $query = (string) $request->query->get('q', '');

        if (!in_array($role, ['all', 'admin', 'manager', 'collaborator'], true)) {
            $role = 'all';
        }

        $users = $this->userRepository->findByRoleAndSearch($role, $query);

        if ($request->isXmlHttpRequest()) {
            return $this->render('admin/users/_table.html.twig', [
                'users' => $users,
            ]);
        }

        return $this->render('admin/users/index.html.twig', [
            'users' => $users,
            'selectedRole' => $role,
            'query' => $query,
        ]);
    }

    #[Route('/{id}/toggle-status', name: 'app_admin_users_toggle_status', methods: ['POST'])]
    public function toggleStatus(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);
        $currentUser = $this->getUser();

        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Prevent admin from disabling themselves
        if ($currentUser instanceof User && $user->getIdUser() === $currentUser->getIdUser()) {
            $this->addFlash('error', 'You cannot disable your own account.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Verify CSRF token
        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('toggle-status-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $user->setEnabled(!$user->isEnabled());
        $this->entityManager->flush();

        $status = $user->isEnabled() ? 'enabled' : 'disabled';
        $this->addFlash('success', sprintf('User "%s" has been %s successfully.', $user->getName(), $status));

        return $this->redirectToRoute('app_admin_users_index');
    }

    #[Route('/{id}/delete', name: 'app_admin_users_delete', methods: ['POST'])]
    public function delete(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);
        $currentUser = $this->getUser();

        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Prevent admin from deleting themselves
        if ($currentUser instanceof User && $user->getIdUser() === $currentUser->getIdUser()) {
            $this->addFlash('error', 'You cannot delete your own account.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Verify CSRF token
        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('delete-user-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $userName = $user->getName();
        $this->entityManager->remove($user);
        $this->entityManager->flush();

        $this->addFlash('success', sprintf('User "%s" has been deleted successfully.', $userName));

        return $this->redirectToRoute('app_admin_users_index');
    }
}

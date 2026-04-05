<?php

namespace App\Controller\Admin;

use App\Entity\Role;
use App\Entity\User;
use App\Form\Admin\UserType;
use App\Repository\UserRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Symfony\Component\Routing\Attribute\Route;
use Symfony\Component\Security\Http\Attribute\IsGranted;

#[Route('/admin/users')]
#[IsGranted('ROLE_ADMIN')]
class UserController extends AbstractController
{
    private const ALLOWED_ROLE_NAMES = ['ADMIN', 'AGENT', 'USER'];

    public function __construct(
        private readonly UserRepository $userRepository,
        private readonly EntityManagerInterface $entityManager,
        private readonly UserPasswordHasherInterface $passwordHasher
    ) {
    }

    #[Route('', name: 'app_admin_users_index', methods: ['GET'])]
    public function index(Request $request): Response
    {
        $role = strtolower((string) $request->query->get('role', 'all'));
        $query = (string) $request->query->get('q', '');

        if (!in_array($role, ['all', 'admin', 'agent', 'user'], true)) {
            $role = 'all';
        }

        $users = $this->userRepository->findByRoleAndSearch($role, $query);
        $allUsers = $this->userRepository->findByRoleAndSearch('all', '');
        $stats = [
            'total' => count($allUsers),
            'admin' => 0,
            'agent' => 0,
            'user' => 0,
            'active' => 0,
        ];

        foreach ($allUsers as $statUser) {
            $roleName = strtoupper((string) ($statUser->getRole()?->getName() ?? 'USER'));
            if ($roleName === 'ADMIN') {
                ++$stats['admin'];
            } elseif ($roleName === 'AGENT') {
                ++$stats['agent'];
            } else {
                ++$stats['user'];
            }

            if ($statUser->isActive()) {
                ++$stats['active'];
            }
        }

        $stats['blocked'] = $stats['total'] - $stats['active'];

        $roles = $this->entityManager->getRepository(Role::class)->createQueryBuilder('r')
            ->andWhere('r.name IN (:allowed)')
            ->setParameter('allowed', self::ALLOWED_ROLE_NAMES)
            ->orderBy('r.name', 'ASC')
            ->getQuery()
            ->getResult();

        if ($request->isXmlHttpRequest()) {
            return $this->render('admin/users/_table.html.twig', [
                'users' => $users,
                'roles' => $roles,
            ]);
        }

        return $this->render('admin/users/index.html.twig', [
            'users' => $users,
            'roles' => $roles,
            'selectedRole' => $role,
            'query' => $query,
            'stats' => $stats,
        ]);
    }

    #[Route('/new', name: 'app_admin_users_new', methods: ['GET', 'POST'])]
    public function new(Request $request): Response
    {
        $user = new User();
        $form = $this->createForm(UserType::class, $user, [
            'require_password' => true,
        ]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = (string) $form->get('plainPassword')->getData();
            $user->setPassword($this->passwordHasher->hashPassword($user, $plainPassword));

            $this->entityManager->persist($user);
            $this->entityManager->flush();

            $this->addFlash('success', sprintf('User "%s" has been created successfully.', (string) $user->getEmail()));

            return $this->redirectToRoute('app_admin_users_show', [
                'id' => $user->getId(),
            ]);
        }

        if ($form->isSubmitted() && !$form->isValid()) {
            $this->addFlash('error', 'Please fix the validation errors and try again.');
        }

        return $this->render('admin/users/new.html.twig', [
            'user' => $user,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_admin_users_show', methods: ['GET'], requirements: ['id' => '\\d+'])]
    public function show(User $user): Response
    {
        return $this->render('admin/users/show.html.twig', [
            'user' => $user,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_admin_users_edit', methods: ['GET', 'POST'], requirements: ['id' => '\\d+'])]
    public function edit(Request $request, User $user): Response
    {
        $form = $this->createForm(UserType::class, $user, [
            'require_password' => false,
        ]);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $plainPassword = (string) $form->get('plainPassword')->getData();
            if ($plainPassword !== '') {
                $user->setPassword($this->passwordHasher->hashPassword($user, $plainPassword));
            }

            $this->entityManager->flush();

            $this->addFlash('success', sprintf('User "%s" has been updated successfully.', (string) $user->getEmail()));

            return $this->redirectToRoute('app_admin_users_show', [
                'id' => $user->getId(),
            ]);
        }

        if ($form->isSubmitted() && !$form->isValid()) {
            $this->addFlash('error', 'Please fix the validation errors and try again.');
        }

        return $this->render('admin/users/edit.html.twig', [
            'user' => $user,
            'form' => $form,
        ]);
    }

    #[Route('/{id}/toggle-active', name: 'app_admin_users_toggle_active', methods: ['POST'], requirements: ['id' => '\\d+'])]
    public function toggleActive(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);
        $currentUser = $this->getUser();

        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Prevent admin from disabling themselves
        if ($currentUser instanceof User && $user->getId() === $currentUser->getId()) {
            $this->addFlash('error', 'You cannot disable your own account.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Verify CSRF token
        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('toggle-active-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $user->setIsActive(!$user->isActive());
        $this->entityManager->flush();

        $status = $user->isActive() ? 'enabled' : 'disabled';
        $this->addFlash('success', sprintf('User "%s" has been %s successfully.', $user->getEmail(), $status));

        return $this->redirectToRoute('app_admin_users_index');
    }

    #[Route('/{id}/toggle-verified', name: 'app_admin_users_toggle_verified', methods: ['POST'], requirements: ['id' => '\\d+'])]
    public function toggleVerified(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);

        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('toggle-verified-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $user->setIsVerified(!$user->isVerified());
        $this->entityManager->flush();

        $status = $user->isVerified() ? 'verified' : 'unverified';
        $this->addFlash('success', sprintf('User "%s" is now %s.', $user->getEmail(), $status));

        return $this->redirectToRoute('app_admin_users_index');
    }

    #[Route('/{id}/change-role', name: 'app_admin_users_change_role', methods: ['POST'], requirements: ['id' => '\\d+'])]
    public function changeRole(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);
        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('change-role-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $roleId = (int) $request->request->get('role_id', 0);
        $role = $this->entityManager->getRepository(Role::class)->find($roleId);
        if (!$role || !in_array(strtoupper((string) $role->getName()), self::ALLOWED_ROLE_NAMES, true)) {
            $this->addFlash('error', 'Selected role does not exist.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $user->setRole($role);
        $this->entityManager->flush();
        $this->addFlash('success', sprintf('Role updated for "%s".', $user->getEmail()));

        return $this->redirectToRoute('app_admin_users_index');
    }

    #[Route('/{id}/delete', name: 'app_admin_users_delete', methods: ['POST'], requirements: ['id' => '\\d+'])]
    public function delete(int $id, Request $request): Response
    {
        $user = $this->userRepository->find($id);
        $currentUser = $this->getUser();

        if (!$user) {
            $this->addFlash('error', 'User not found.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Prevent admin from deleting themselves
        if ($currentUser instanceof User && $user->getId() === $currentUser->getId()) {
            $this->addFlash('error', 'You cannot delete your own account.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        // Verify CSRF token
        $token = $request->request->get('_token');
        if (!$this->isCsrfTokenValid('delete-user-' . $id, $token)) {
            $this->addFlash('error', 'Invalid CSRF token.');
            return $this->redirectToRoute('app_admin_users_index');
        }

        $userName = $user->getEmail();
        $this->entityManager->remove($user);
        $this->entityManager->flush();

        $this->addFlash('success', sprintf('User "%s" has been deleted successfully.', $userName));

        return $this->redirectToRoute('app_admin_users_index');
    }
}

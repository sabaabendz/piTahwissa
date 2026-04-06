<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\Routing\Attribute\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home', methods: ['GET'])]
    public function index(): RedirectResponse
    {
        return $this->redirect('/frontoffice/index.html');
    }

    #[Route('/frontoffice', name: 'app_frontoffice_root', methods: ['GET'])]
    #[Route('/frontoffice/', name: 'app_frontoffice_root_slash', methods: ['GET'])]
    public function frontofficeRoot(): RedirectResponse
    {
        return $this->redirect('/frontoffice/index.html');
    }
}

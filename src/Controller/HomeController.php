<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home', methods: ['GET'])]
    public function index(): Response
    {
        // If user is not authenticated, redirect to the login page
        if (!$this->getUser()) {
            return $this->redirectToRoute('app_login');
        }

        // If authenticated, render the home page (or dashboard)
        return $this->render('front/home.html.twig');
    }
}

<?php

require_once __DIR__.'/vendor/autoload.php';

use Symfony\Component\Dotenv\Dotenv;
use Symfony\Component\Mailer\Mailer;
use Symfony\Component\Mailer\Transport;
use Symfony\Component\Mime\Email;

// Load .env
$dotenv = new Dotenv();
$dotenv->load(__DIR__.'/.env');

echo "Configuration:\n";
echo "MAILER_DSN: " . $_ENV['MAILER_DSN'] . "\n";
echo "ADMIN_EMAIL: " . $_ENV['ADMIN_EMAIL'] . "\n\n";

try {
    // Create transport
    $transport = Transport::fromDsn($_ENV['MAILER_DSN']);
    $mailer = new Mailer($transport);
    
    // Create email
    $email = (new Email())
        ->from('noreply@yourapp.com')
        ->to($_ENV['ADMIN_EMAIL'])
        ->subject('Test Email - Symfony')
        ->html('<h1>Test Email</h1><p>Ceci est un email de test envoyé le ' . date('d/m/Y à H:i:s') . '</p>');
    
    echo "Envoi de l'email...\n";
    $mailer->send($email);
    echo "✅ Email envoyé avec succès à " . $_ENV['ADMIN_EMAIL'] . "\n";
    echo "Vérifiez votre boîte de réception (et le dossier spam).\n";
    
} catch (\Exception $e) {
    echo "❌ Erreur: " . $e->getMessage() . "\n";
    echo "Trace: " . $e->getTraceAsString() . "\n";
}

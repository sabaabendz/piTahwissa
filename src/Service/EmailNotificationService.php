<?php

namespace App\Service;

use App\Entity\Projet;
use Symfony\Component\Mailer\MailerInterface;
use Symfony\Component\Mime\Email;

class EmailNotificationService
{
    private MailerInterface $mailer;
    private string $adminEmail;

    public function __construct(MailerInterface $mailer, string $adminEmail)
    {
        $this->mailer = $mailer;
        $this->adminEmail = $adminEmail;
    }

    public function notifyProjectUpdated(Projet $projet): void
    {
        $email = (new Email())
            ->from('noreply@yourapp.com')
            ->to($this->adminEmail)
            ->subject('Projet mis à jour: ' . $projet->getNom())
            ->html($this->getUpdateEmailContent($projet));

        try {
            $this->mailer->send($email);
        } catch (\Exception $e) {
            throw new \Exception('Erreur envoi email: ' . $e->getMessage());
        }
    }

    public function notifyProjectDeleted(string $projectName): void
    {
        $email = (new Email())
            ->from('noreply@yourapp.com')
            ->to($this->adminEmail)
            ->subject('Projet supprimé: ' . $projectName)
            ->html($this->getDeleteEmailContent($projectName));

        try {
            $this->mailer->send($email);
        } catch (\Exception $e) {
            throw new \Exception('Erreur envoi email: ' . $e->getMessage());
        }
    }

    private function getUpdateEmailContent(Projet $projet): string
    {
        return '
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Projet mis à jour</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #f4f4f4;">
    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: bold;">
                                📝 Projet Mis à Jour
                            </h1>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <p style="margin: 0 0 20px; font-size: 16px; color: #333333; line-height: 1.6;">
                                Le projet <strong style="color: #667eea;">' . htmlspecialchars($projet->getNom()) . '</strong> a été modifié.
                            </p>
                            
                            <!-- Project Details Card -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 6px; margin: 20px 0;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <!-- Project Name -->
                                        <div style="margin-bottom: 15px;">
                                            <div style="color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 5px;">
                                                Nom du projet
                                            </div>
                                            <div style="color: #212529; font-size: 18px; font-weight: bold;">
                                                ' . htmlspecialchars($projet->getNom()) . '
                                            </div>
                                        </div>
                                        
                                        <!-- Description -->
                                        ' . ($projet->getDescription() ? '
                                        <div style="margin-bottom: 15px;">
                                            <div style="color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 5px;">
                                                Description
                                            </div>
                                            <div style="color: #495057; font-size: 14px; line-height: 1.6;">
                                                ' . nl2br(htmlspecialchars($projet->getDescription())) . '
                                            </div>
                                        </div>
                                        ' : '') . '
                                        
                                        <!-- Status Badge -->
                                        <div style="margin-bottom: 15px;">
                                            <div style="color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 5px;">
                                                Statut
                                            </div>
                                            <div>
                                                <span style="display: inline-block; padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: bold; ' . 
                                                ($projet->getStatut() === 'actif' ? 'background-color: #0d6efd; color: #ffffff;' : 
                                                ($projet->getStatut() === 'termine' ? 'background-color: #198754; color: #ffffff;' : 
                                                'background-color: #6c757d; color: #ffffff;')) . '">
                                                    ' . ($projet->getStatut() === 'actif' ? '🟢 Actif' : 
                                                    ($projet->getStatut() === 'termine' ? '✅ Terminé' : '⏸️ En attente')) . '
                                                </span>
                                            </div>
                                        </div>
                                        
                                        <!-- Dates -->
                                        <table width="100%" cellpadding="0" cellspacing="0" style="margin-top: 15px;">
                                            <tr>
                                                <td width="50%" style="padding-right: 10px;">
                                                    <div style="background-color: #ffffff; padding: 15px; border-radius: 6px; border-left: 4px solid #0d6efd;">
                                                        <div style="color: #6c757d; font-size: 11px; text-transform: uppercase; margin-bottom: 5px;">
                                                            📅 Date de début
                                                        </div>
                                                        <div style="color: #212529; font-size: 16px; font-weight: bold;">
                                                            ' . $projet->getDateDebut()->format('d/m/Y') . '
                                                        </div>
                                                    </div>
                                                </td>
                                                <td width="50%" style="padding-left: 10px;">
                                                    <div style="background-color: #ffffff; padding: 15px; border-radius: 6px; border-left: 4px solid #dc3545;">
                                                        <div style="color: #6c757d; font-size: 11px; text-transform: uppercase; margin-bottom: 5px;">
                                                            ⏰ Date d\'échéance
                                                        </div>
                                                        <div style="color: #212529; font-size: 16px; font-weight: bold;">
                                                            ' . $projet->getDateEcheance()->format('d/m/Y') . '
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Timestamp -->
                            <p style="margin: 20px 0 0; font-size: 13px; color: #6c757d; text-align: center;">
                                📧 Notification envoyée le ' . (new \DateTime())->format('d/m/Y à H:i:s') . '
                            </p>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6;">
                            <p style="margin: 0; font-size: 12px; color: #6c757d;">
                                Système de Gestion de Projets
                            </p>
                            <p style="margin: 5px 0 0; font-size: 11px; color: #adb5bd;">
                                Cet email a été envoyé automatiquement, merci de ne pas y répondre.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>';
    }

    private function getDeleteEmailContent(string $projectName): string
    {
        return '
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Projet supprimé</title>
</head>
<body style="margin: 0; padding: 0; font-family: Arial, Helvetica, sans-serif; background-color: #f4f4f4;">
    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f4f4f4; padding: 20px;">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); padding: 30px; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: bold;">
                                🗑️ Projet Supprimé
                            </h1>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <!-- Alert Box -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #fff3cd; border-left: 4px solid #ffc107; border-radius: 6px; margin-bottom: 20px;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <div style="color: #856404; font-size: 14px; line-height: 1.6;">
                                            <strong>⚠️ Action importante</strong><br>
                                            Un projet a été définitivement supprimé du système.
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Project Info -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f8f9fa; border-radius: 6px; margin: 20px 0;">
                                <tr>
                                    <td style="padding: 25px; text-align: center;">
                                        <div style="font-size: 48px; margin-bottom: 15px;">
                                            📦
                                        </div>
                                        <div style="color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px;">
                                            Projet supprimé
                                        </div>
                                        <div style="color: #212529; font-size: 22px; font-weight: bold; margin-bottom: 15px;">
                                            ' . htmlspecialchars($projectName) . '
                                        </div>
                                        <div style="display: inline-block; background-color: #dc3545; color: #ffffff; padding: 8px 16px; border-radius: 20px; font-size: 12px; font-weight: bold;">
                                            ❌ SUPPRIMÉ
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Details -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border: 1px solid #dee2e6; border-radius: 6px; margin: 20px 0;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <table width="100%" cellpadding="8" cellspacing="0">
                                            <tr>
                                                <td style="color: #6c757d; font-size: 13px; width: 40%;">
                                                    🗓️ Date de suppression
                                                </td>
                                                <td style="color: #212529; font-size: 14px; font-weight: bold;">
                                                    ' . (new \DateTime())->format('d/m/Y') . '
                                                </td>
                                            </tr>
                                            <tr>
                                                <td style="color: #6c757d; font-size: 13px; border-top: 1px solid #f8f9fa; padding-top: 12px;">
                                                    ⏰ Heure de suppression
                                                </td>
                                                <td style="color: #212529; font-size: 14px; font-weight: bold; border-top: 1px solid #f8f9fa; padding-top: 12px;">
                                                    ' . (new \DateTime())->format('H:i:s') . '
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Info Note -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #e7f3ff; border-left: 4px solid #0d6efd; border-radius: 6px; margin-top: 20px;">
                                <tr>
                                    <td style="padding: 15px;">
                                        <div style="color: #084298; font-size: 13px; line-height: 1.6;">
                                            ℹ️ <strong>Information:</strong> Toutes les tâches associées à ce projet ont également été supprimées.
                                        </div>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Timestamp -->
                            <p style="margin: 20px 0 0; font-size: 13px; color: #6c757d; text-align: center;">
                                📧 Notification envoyée le ' . (new \DateTime())->format('d/m/Y à H:i:s') . '
                            </p>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #dee2e6;">
                            <p style="margin: 0; font-size: 12px; color: #6c757d;">
                                Système de Gestion de Projets
                            </p>
                            <p style="margin: 5px 0 0; font-size: 11px; color: #adb5bd;">
                                Cet email a été envoyé automatiquement, merci de ne pas y répondre.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>';
    }
}

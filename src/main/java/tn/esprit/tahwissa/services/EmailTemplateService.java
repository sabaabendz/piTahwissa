package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.models.Destination;
import tn.esprit.tahwissa.models.PointInteret;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service de génération de templates HTML pour les emails
 */
public class EmailTemplateService {

    // ==================== TEMPLATE : DESTINATION CRÉÉE ====================
    public static String getDestinationCreatedTemplate(Destination destination) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #F3F4F6; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #4F46E5 0%%, #9333EA 100%%); color: white; padding: 30px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; }
                    .content { padding: 30px; }
                    .info-box { background: #EEF2FF; border-left: 4px solid #4F46E5; padding: 15px; margin: 15px 0; border-radius: 6px; }
                    .info-box strong { color: #4F46E5; display: block; margin-bottom: 5px; }
                    .footer { background: #F9FAFB; padding: 20px; text-align: center; font-size: 12px; color: #6B7280; }
                    .btn { display: inline-block; background: #3B82F6; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✈️ Tahwissa Travel Management</h1>
                        <p style="margin: 10px 0 0 0;">Nouvelle destination ajoutée</p>
                    </div>
                    <div class="content">
                        <h2 style="color: #1F2937;">🎉 Destination créée avec succès !</h2>
                        <p>Bonjour,</p>
                        <p>La destination suivante a été ajoutée à votre système de gestion :</p>
                        
                        <div class="info-box">
                            <strong>📍 Nom</strong>
                            %s
                        </div>
                        
                        <div class="info-box">
                            <strong>🌍 Pays</strong>
                            %s
                        </div>
                        
                        <div class="info-box">
                            <strong>🏙️ Ville</strong>
                            %s
                        </div>
                        
                        <div class="info-box">
                            <strong>📝 Description</strong>
                            %s
                        </div>
                        
                        %s
                        
                        <p style="margin-top: 20px; color: #6B7280; font-size: 14px;">
                            Date de création : %s
                        </p>
                        
                        <a href="#" class="btn">Voir dans l'application</a>
                    </div>
                    <div class="footer">
                        <p>© 2026 Tahwissa Travel Management. Tous droits réservés.</p>
                        <p>Cet email a été généré automatiquement, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                destination.getNom(),
                destination.getPays(),
                destination.getVille() != null ? destination.getVille() : "Non spécifiée",
                destination.getDescription() != null ? destination.getDescription() : "Aucune description",
                destination.getLatitude() != null && destination.getLongitude() != null ?
                        String.format("<div class=\"info-box\"><strong>🗺️ Coordonnées</strong>Lat: %s, Long: %s</div>",
                                destination.getLatitude(), destination.getLongitude()) : "",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
        );
    }

    // ==================== TEMPLATE : POINT D'INTÉRÊT CRÉÉ ====================
    public static String getPointInteretCreatedTemplate(PointInteret pointInteret, String destinationName) {
        String typeColor = switch (pointInteret.getType().toLowerCase()) {
            case "monument" -> "#7C3AED";
            case "plage" -> "#2563EB";
            case "musée" -> "#9333EA";
            case "restaurant" -> "#D97706";
            case "parc" -> "#059669";
            default -> "#6B7280";
        };

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #F3F4F6; margin: 0; padding: 20px; }
                    .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #EC4899 0%%, #8B5CF6 100%%); color: white; padding: 30px 20px; text-align: center; }
                    .content { padding: 30px; }
                    .badge { display: inline-block; background: %s; color: white; padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: bold; text-transform: uppercase; }
                    .info-box { background: #F3F4F6; padding: 15px; margin: 15px 0; border-radius: 8px; }
                    .footer { background: #F9FAFB; padding: 20px; text-align: center; font-size: 12px; color: #6B7280; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏖️ Nouveau Point d'Intérêt</h1>
                    </div>
                    <div class="content">
                        <h2>%s</h2>
                        <span class="badge">%s</span>
                        
                        <div class="info-box">
                            <strong>📍 Destination :</strong> %s
                        </div>
                        
                        <div class="info-box">
                            <strong>📝 Description :</strong><br>
                            %s
                        </div>
                        
                        <p style="color: #6B7280; font-size: 14px; margin-top: 20px;">
                            Ajouté le : %s
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2026 Tahwissa Travel Management</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                typeColor,
                pointInteret.getNom(),
                pointInteret.getType(),
                destinationName,
                pointInteret.getDescription() != null ? pointInteret.getDescription() : "Aucune description",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
        );
    }

    // ==================== TEMPLATE : RAPPORT HEBDOMADAIRE ====================
    public static String getWeeklyReportTemplate(int totalDestinations, int totalPoints, List<String> recentDestinations) {
        StringBuilder recentList = new StringBuilder();
        for (String dest : recentDestinations) {
            recentList.append("<li style=\"margin: 8px 0;\">").append(dest).append("</li>");
        }

        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #F3F4F6; margin: 0; padding: 20px; }
                    .container { max-width: 650px; margin: 0 auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #3B82F6 0%%, #1E40AF 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .stats { display: flex; justify-content: space-around; padding: 30px 20px; background: #F9FAFB; }
                    .stat-box { text-align: center; }
                    .stat-number { font-size: 36px; font-weight: bold; color: #3B82F6; display: block; }
                    .stat-label { font-size: 14px; color: #6B7280; margin-top: 5px; }
                    .content { padding: 30px; }
                    .recent-list { background: #EFF6FF; padding: 20px; border-radius: 8px; }
                    .footer { background: #F9FAFB; padding: 20px; text-align: center; font-size: 12px; color: #6B7280; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📊 Rapport Hebdomadaire</h1>
                        <p>Semaine du %s</p>
                    </div>
                    
                    <div class="stats">
                        <div class="stat-box">
                            <span class="stat-number">%d</span>
                            <span class="stat-label">Destinations</span>
                        </div>
                        <div class="stat-box">
                            <span class="stat-number">%d</span>
                            <span class="stat-label">Points d'Intérêt</span>
                        </div>
                    </div>
                    
                    <div class="content">
                        <h3>🆕 Destinations récentes :</h3>
                        <div class="recent-list">
                            <ul style="margin: 0; padding-left: 20px;">
                                %s
                            </ul>
                        </div>
                        
                        <p style="margin-top: 30px; color: #6B7280;">
                            Continuez votre excellent travail ! 🎉
                        </p>
                    </div>
                    
                    <div class="footer">
                        <p>© 2026 Tahwissa Travel Management</p>
                        <p>Rapport généré automatiquement</p>
                    </div>
                </div>
            </body>
            </html>
            """,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                totalDestinations,
                totalPoints,
                recentList.toString()
        );
    }
}
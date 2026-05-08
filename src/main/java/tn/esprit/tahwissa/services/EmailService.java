package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.config.EmailConfig;
import tn.esprit.tahwissa.models.Destination;
import tn.esprit.tahwissa.models.PointInteret;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service d'envoi d'emails avec JavaMail
 */
public class EmailService {

    private final Session session;

    public EmailService() {
        this.session = EmailConfig.getEmailSession();
    }

    // ==================== MÉTHODE GÉNÉRIQUE D'ENVOI ====================
    /**
     * Envoie un email simple (texte brut)
     */
    public void sendSimpleEmail(String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(EmailConfig.getEmailFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
        System.out.println("✅ Email envoyé à : " + to);
    }

    /**
     * Envoie un email HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        Message message = new MimeMessage(session);

        message.setFrom(new InternetAddress(EmailConfig.getEmailFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlBody, "text/html; charset=utf-8");

        Transport.send(message);
        System.out.println("✅ Email HTML envoyé à : " + to);
    }

    // ==================== EMAILS SPÉCIFIQUES ====================

    /**
     * Email de confirmation : Destination créée
     */
    public void sendDestinationCreatedEmail(String to, Destination destination) {
        try {
            String subject = "✅ Nouvelle destination créée : " + destination.getNom();

            String htmlBody = EmailTemplateService.getDestinationCreatedTemplate(destination);

            sendHtmlEmail(to, subject, htmlBody);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email destination : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Email de confirmation : Point d'intérêt ajouté
     */
    public void sendPointInteretCreatedEmail(String to, PointInteret pointInteret, String destinationName) {
        try {
            String subject = "📍 Nouveau point d'intérêt ajouté : " + pointInteret.getNom();

            String htmlBody = EmailTemplateService.getPointInteretCreatedTemplate(pointInteret, destinationName);

            sendHtmlEmail(to, subject, htmlBody);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email point d'intérêt : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Email de rapport hebdomadaire
     */
    public void sendWeeklyReport(String to, int totalDestinations, int totalPoints, List<String> recentDestinations) {
        try {
            String subject = "📊 Rapport hebdomadaire Tahwissa - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String htmlBody = EmailTemplateService.getWeeklyReportTemplate(
                    totalDestinations, totalPoints, recentDestinations
            );

            sendHtmlEmail(to, subject, htmlBody);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi rapport hebdomadaire : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Email de notification : Destination modifiée
     */
    public void sendDestinationUpdatedEmail(String to, Destination destination) {
        try {
            String subject = "✏️ Destination modifiée : " + destination.getNom();

            String body = String.format(
                    "Bonjour,\n\n" +
                            "La destination '%s' a été modifiée avec succès.\n\n" +
                            "Détails :\n" +
                            "- Pays : %s\n" +
                            "- Ville : %s\n" +
                            "- Description : %s\n\n" +
                            "Date de modification : %s\n\n" +
                            "Cordialement,\n" +
                            "L'équipe Tahwissa",
                    destination.getNom(),
                    destination.getPays(),
                    destination.getVille(),
                    destination.getDescription(),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            sendSimpleEmail(to, subject, body);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Email de notification : Destination supprimée
     */
    public void sendDestinationDeletedEmail(String to, String destinationName) {
        try {
            String subject = "🗑️ Destination supprimée : " + destinationName;

            String body = String.format(
                    "Bonjour,\n\n" +
                            "La destination '%s' a été supprimée de la base de données.\n\n" +
                            "Date de suppression : %s\n\n" +
                            "Cordialement,\n" +
                            "L'équipe Tahwissa",
                    destinationName,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );

            sendSimpleEmail(to, subject, body);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur envoi email suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test de connexion SMTP
     */
    public boolean testConnection() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            transport.close();
            System.out.println("✅ Connexion SMTP réussie !");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Connexion SMTP échouée : " + e.getMessage());
            return false;
        }
    }
}
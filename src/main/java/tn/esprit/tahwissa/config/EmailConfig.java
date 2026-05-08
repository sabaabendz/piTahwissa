package tn.esprit.tahwissa.config;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * Configuration SMTP pour l'envoi d'emails via Gmail
 */
public class EmailConfig {

    // ==================== CONFIGURATION SMTP ====================
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "ahmedbelkhiria07@gmail.com"; // CHANGE avec ton email
    private static final String EMAIL_PASSWORD = "bqbnbjaithxdwtbw"; // CHANGE avec ton mot de passe d'application

    /**
     * Crée une session email avec authentification Gmail
     */
    public static Session getEmailSession() {
        Properties properties = new Properties();

        // Configuration SMTP Gmail
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_HOST);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Authentification
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        };

        return Session.getInstance(properties, auth);
    }

    public static String getEmailFrom() {
        return EMAIL_FROM;
    }

    // ==================== CONFIGURATION ALTERNATIVE (Mailtrap pour tests) ====================
    /**
     * Session de test avec Mailtrap (pour développement)
     * Inscription gratuite sur https://mailtrap.io/
     */
    public static Session getTestEmailSession() {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.mailtrap.io");
        properties.put("mail.smtp.port", "2525");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your_mailtrap_username", "your_mailtrap_password");
            }
        };

        return Session.getInstance(properties, auth);
    }
}
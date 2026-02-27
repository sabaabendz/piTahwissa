package tools;

import services.MailService;
import services.PasswordResetService;

public class MailSenderCli {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: MailSenderCli <email> [name]");
            System.exit(1);
        }

        String email = args[0].trim();
        String name = args.length > 1 ? args[1].trim() : "";

        MailService mailService = new MailService();
        PasswordResetService resetService = new PasswordResetService();
        PasswordResetService.ResetToken token = resetService.createResetToken(email);

        try {
            mailService.sendPasswordResetEmail(email, name, token.getCode());
            System.out.println("OK: reset email sent to " + email);
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}


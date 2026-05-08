package tn.esprit.tahwissa.controllers.client;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.tahwissa.models.Paiement;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.services.PaiementService;
import tn.esprit.tahwissa.services.payment.StripePaymentService;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.MailService;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Stripe card payment dialog.
 *
 * Launched modally after a reservation is confirmed.
 * Collects card details, calls Stripe in a background thread,
 * then updates the Paiement record in the DB and shows the result.
 */
public class PaymentDialogController {

    // ─── FXML Bindings ───────────────────────────────────────────────────────

    @FXML private Label    amountLabel;
    @FXML private Label    voyageLabel;

    // Live card preview
    @FXML private Label cardPreviewNumber;
    @FXML private Label cardPreviewExpiry;
    @FXML private Label cardPreviewName;

    // Input fields
    @FXML private TextField cardNumberField;
    @FXML private TextField expiryField;
    @FXML private TextField cvcField;
    @FXML private TextField cardholderNameField;

    // State widgets
    @FXML private Button          payButton;
    @FXML private Button          cancelButton;
    @FXML private Label           statusLabel;
    @FXML private ProgressIndicator progressIndicator;

    // Result overlay
    @FXML private VBox  resultPane;
    @FXML private Label resultIcon;
    @FXML private Label resultTitle;
    @FXML private Label resultMessage;

    // ─── State ───────────────────────────────────────────────────────────────

    private ReservationVoyage   reservation;
    private Paiement            paiement;
    private PaiementService     paiementService;
    private StripePaymentService stripeService;
    private Stage               stage;
    private boolean             paymentSuccessful = false;

    // ─── Initialization ───────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        stripeService = new StripePaymentService();

        // ── Card number: auto-format as XXXX XXXX XXXX XXXX ─────────────────
        cardNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
            String digits = newVal.replaceAll("[^0-9]", "");
            if (digits.length() > 16) digits = digits.substring(0, 16);
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < digits.length(); i++) {
                if (i > 0 && i % 4 == 0) formatted.append(' ');
                formatted.append(digits.charAt(i));
            }
            String result = formatted.toString();
            if (!result.equals(newVal)) {
                cardNumberField.setText(result);
                cardNumberField.positionCaret(result.length());
            }
            updateCardPreview();
        });

        // ── Expiry: auto-format as MM/YY ─────────────────────────────────────
        expiryField.textProperty().addListener((obs, oldVal, newVal) -> {
            String digits = newVal.replaceAll("[^0-9]", "");
            if (digits.length() > 4) digits = digits.substring(0, 4);
            String result = digits.length() > 2
                    ? digits.substring(0, 2) + "/" + digits.substring(2)
                    : digits;
            if (!result.equals(newVal)) {
                expiryField.setText(result);
                expiryField.positionCaret(result.length());
            }
            updateCardPreview();
        });

        // ── CVC: digits only, max 4 ───────────────────────────────────────────
        cvcField.textProperty().addListener((obs, oldVal, newVal) -> {
            String digits = newVal.replaceAll("[^0-9]", "");
            if (digits.length() > 4) digits = digits.substring(0, 4);
            if (!digits.equals(newVal)) cvcField.setText(digits);
        });

        // ── Name: update card preview ─────────────────────────────────────────
        cardholderNameField.textProperty().addListener((obs, oldVal, newVal) -> updateCardPreview());
    }

    // ─── Data Injection (called before showAndWait) ───────────────────────────

    public void setData(ReservationVoyage reservation,
                        Paiement paiement,
                        PaiementService paiementService,
                        Stage stage) {
        this.reservation    = reservation;
        this.paiement       = paiement;
        this.paiementService = paiementService;
        this.stage          = stage;

        String amountText = paiement.getMontant() + " DT";
        amountLabel.setText(amountText);

        String voyageName = paiement.getTitreVoyage() != null
                ? paiement.getTitreVoyage()
                : "Réservation #" + reservation.getId();
        voyageLabel.setText(voyageName);

        payButton.setText("💳 Payer " + amountText);
    }

    // ─── Card Preview ─────────────────────────────────────────────────────────

    private void updateCardPreview() {
        // Card number preview (show dots for missing digits)
        String digits = cardNumberField.getText().replaceAll("[^0-9]", "");
        StringBuilder preview = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            if (i > 0 && i % 4 == 0) preview.append(' ');
            preview.append(i < digits.length() ? digits.charAt(i) : '•');
        }
        cardPreviewNumber.setText(preview.toString());

        // Expiry preview
        String expiry = expiryField.getText().trim();
        cardPreviewExpiry.setText(expiry.isEmpty() ? "MM/YY" : expiry);

        // Name preview
        String name = cardholderNameField.getText().trim();
        cardPreviewName.setText(name.isEmpty() ? "VOTRE NOM" : name.toUpperCase());
    }

    // ─── Payment Handler ──────────────────────────────────────────────────────

    @FXML
    public void handlePayment() {
        statusLabel.setText("");

        // ── Validate inputs ───────────────────────────────────────────────────
        String cardNum = cardNumberField.getText().replaceAll("\\s", "");
        String expiry  = expiryField.getText().trim();
        String cvc     = cvcField.getText().trim();
        String name    = cardholderNameField.getText().trim();

        if (cardNum.length() < 13 || cardNum.length() > 16) {
            showInlineError("Numéro de carte invalide (13–16 chiffres)");
            return;
        }
        if (!expiry.contains("/") || expiry.length() < 5) {
            showInlineError("Date d'expiration invalide — format MM/YY attendu");
            return;
        }
        if (cvc.length() < 3) {
            showInlineError("CVC invalide (minimum 3 chiffres)");
            return;
        }
        if (name.isEmpty()) {
            showInlineError("Veuillez entrer le nom du titulaire");
            return;
        }

        // ── Parse expiry ──────────────────────────────────────────────────────
        int expMonth, expYear;
        try {
            String[] parts = expiry.split("/");
            expMonth = Integer.parseInt(parts[0]);
            expYear  = 2000 + Integer.parseInt(parts[1]);
            if (expMonth < 1 || expMonth > 12)
                throw new NumberFormatException("invalid month");
        } catch (Exception ex) {
            showInlineError("Date d'expiration invalide");
            return;
        }

        // ── Disable form & show spinner ───────────────────────────────────────
        setFormEnabled(false);
        progressIndicator.setVisible(true);
        statusLabel.setText("Traitement en cours…");
        statusLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px; -fx-padding: 8 24 0 24;");

        // ── Background Stripe call ────────────────────────────────────────────
        final int    finalExpMonth = expMonth;
        final int    finalExpYear  = expYear;
        final String finalCardNum  = cardNum;
        final String finalCvc      = cvc;

        Task<StripePaymentService.PaymentResult> task = new Task<>() {
            @Override
            protected StripePaymentService.PaymentResult call() {
                return stripeService.processCardPayment(
                        paiement.getMontant(),
                        finalCardNum,
                        finalExpMonth,
                        finalExpYear,
                        finalCvc,
                        "Réservation #" + reservation.getId()
                                + (paiement.getTitreVoyage() != null
                                   ? " — " + paiement.getTitreVoyage()
                                   : "")
                );
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            StripePaymentService.PaymentResult result = task.getValue();

            if (result.isSuccess()) {
                // Update Paiement in DB
                paiementService.updateStatut(paiement.getId(), Paiement.StatutPaiement.PAYE);
                // Store the Stripe PaymentIntent ID as reference
                updateReferenceInDb(result.getPaymentIntentId());
                
                // Update Reservation status to CONFIRMEE
                try {
                    ReservationVoyageService resService = new ReservationVoyageService();
                    resService.updateReservationStatus(reservation.getId(), ReservationVoyage.StatutReservation.CONFIRMEE);
                } catch (Exception ex) {
                    System.err.println("Could not update reservation status: " + ex.getMessage());
                }

                // Send Confirmation Email
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser != null && currentUser.getEmail() != null) {
                    try {
                        MailService mailService = new MailService();
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String formattedDate = reservation.getDateReservation() != null ? reservation.getDateReservation().format(dtf) : "-";
                        String voyageName = paiement.getTitreVoyage() != null ? paiement.getTitreVoyage() : "Réservation #" + reservation.getId();
                        
                        mailService.sendReservationStatusEmail(
                            currentUser.getEmail(),
                            currentUser.getFirstName() + " " + currentUser.getLastName(),
                            voyageName,
                            "CONFIRMÉE",
                            paiement.getMontant().toString(),
                            formattedDate,
                            result.getPaymentIntentId()
                        );
                    } catch (Exception ex) {
                        System.err.println("Could not send confirmation email: " + ex.getMessage());
                    }
                }

                paymentSuccessful = true;
                showResultOverlay(true,
                        "Paiement réussi ! 🎉",
                        "Votre paiement de " + paiement.getMontant() + " DT a été confirmé.\n"
                        + "Référence : " + result.getPaymentIntentId());
            } else {
                setFormEnabled(true);
                showResultOverlay(false,
                        "Paiement refusé",
                        result.getErrorMessage() != null
                                ? result.getErrorMessage()
                                : "Vérifiez vos informations et réessayez.");
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            setFormEnabled(true);
            showResultOverlay(false, "Erreur inattendue",
                    "Une erreur est survenue : " + task.getException().getMessage());
        }));

        new Thread(task, "stripe-payment-thread").start();
    }

    // ─── Utility ─────────────────────────────────────────────────────────────

    /** Updates the payment reference (Stripe PaymentIntent ID) in the DB. */
    private void updateReferenceInDb(String paymentIntentId) {
        try {
            java.sql.Connection conn = tn.esprit.tahwissa.config.Database.getInstance();
            try (java.sql.PreparedStatement ps = conn.prepareStatement(
                    "UPDATE paiement SET reference = ?, date_paiement = ? WHERE id = ?")) {
                ps.setString(1, paymentIntentId);
                ps.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, paiement.getId());
                ps.executeUpdate();
            }
        } catch (java.sql.SQLException e) {
            System.err.println("⚠️ Could not update payment reference: " + e.getMessage());
        }
    }

    private void setFormEnabled(boolean enabled) {
        cardNumberField.setDisable(!enabled);
        expiryField.setDisable(!enabled);
        cvcField.setDisable(!enabled);
        cardholderNameField.setDisable(!enabled);
        payButton.setDisable(!enabled);
    }

    private void showInlineError(String message) {
        statusLabel.setText("⚠  " + message);
        statusLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px; -fx-padding: 8 24 0 24;");
    }

    private void showResultOverlay(boolean success, String title, String message) {
        resultIcon.setText(success ? "✅" : "❌");
        resultTitle.setText(title);
        resultMessage.setText(message);
        resultTitle.setStyle(success
                ? "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #10B981;"
                : "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #EF4444;");
        resultPane.setVisible(true);
        resultPane.setManaged(true);
    }

    // ─── Button Handlers ──────────────────────────────────────────────────────

    @FXML
    public void handleCancel() {
        if (stage != null) stage.close();
    }

    @FXML
    public void handleClose() {
        if (stage != null) stage.close();
    }

    // ─── Public getter ────────────────────────────────────────────────────────

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }
}

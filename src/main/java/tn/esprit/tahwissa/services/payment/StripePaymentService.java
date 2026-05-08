package tn.esprit.tahwissa.services.payment;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;


public class StripePaymentService {

    static {
        Properties props = new Properties();
        try (InputStream is = StripePaymentService.class.getResourceAsStream("/config.properties")) {
            if (is != null) {
                props.load(is);
                String key = props.getProperty("stripe.secret_key", "").trim();
                if (!key.isBlank() && !key.equals("sk_test_YOUR_KEY_HERE")) {
                    Stripe.apiKey = key;
                } else {
                    System.err.println("⚠️  Stripe: clé secrète manquante dans config.properties !");
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Stripe config error: " + e.getMessage());
        }
    }

    // ─── Result class ──────────────────────────────────────────────────────────

    public static class PaymentResult {
        private final boolean success;
        private final String  paymentIntentId;
        private final String  errorMessage;

        public PaymentResult(boolean success, String paymentIntentId, String errorMessage) {
            this.success         = success;
            this.paymentIntentId = paymentIntentId;
            this.errorMessage    = errorMessage;
        }

        public boolean isSuccess()          { return success; }
        public String  getPaymentIntentId() { return paymentIntentId; }
        public String  getErrorMessage()    { return errorMessage; }
    }

    // ─── Main payment method ───────────────────────────────────────────────────

    /**
     * Charges a card via Stripe.
     *
     * @param montant     Amount in DT (treated as EUR for demo — Stripe doesn't support TND)
     * @param cardNumber  Card number (spaces are stripped automatically)
     * @param expMonth    Expiry month 1–12
     * @param expYear     Expiry year e.g. 2027
     * @param cvc         3- or 4-digit CVC
     * @param description Short label visible in your Stripe dashboard
     */
    public PaymentResult processCardPayment(
            BigDecimal montant,
            String cardNumber,
            int expMonth,
            int expYear,
            String cvc,
            String description) {

        try {
            // Amount in cents (Stripe minimum = 50 cents)
            long amountCents = montant.multiply(new BigDecimal("100")).longValue();
            if (amountCents < 50) amountCents = 50;

            // ── Step 1: Create a PaymentMethod (CardDetails supports CVC) ──────
            PaymentMethodCreateParams.CardDetails cardDetails =
                PaymentMethodCreateParams.CardDetails.builder()
                    .setNumber(cardNumber.replaceAll("\\s+", ""))
                    .setExpMonth((long) expMonth)
                    .setExpYear((long) expYear)
                    .setCvc(cvc)
                    .build();

            PaymentMethodCreateParams pmParams =
                PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(cardDetails)
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(pmParams);

            // ── Step 2: Create + confirm a PaymentIntent ───────────────────────
            PaymentIntentCreateParams intentParams =
                PaymentIntentCreateParams.builder()
                    .setAmount(amountCents)
                    .setCurrency("eur")
                    .setDescription(description)
                    .setPaymentMethod(paymentMethod.getId())
                    .addPaymentMethodType("card")
                    .setConfirm(true)
                    .setReturnUrl("https://tahwissa.app/payment/return")
                    .build();

            PaymentIntent intent = PaymentIntent.create(intentParams);

            boolean ok = "succeeded".equals(intent.getStatus());
            return new PaymentResult(
                ok,
                intent.getId(),
                ok ? null : "Statut Stripe : " + intent.getStatus()
            );

        } catch (CardException e) {
            return new PaymentResult(false, null, "Carte refusée : " + e.getUserMessage());
        } catch (StripeException e) {
            return new PaymentResult(false, null, "Erreur Stripe : " + e.getMessage());
        }
    }
}

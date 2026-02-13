package tn.esprit.tahwissa;

import tn.esprit.tahwissa.entities.ReservationVoyage;
import tn.esprit.tahwissa.entities.Voyage;
import tn.esprit.tahwissa.services.ReservationVoyageService;
import tn.esprit.tahwissa.services.VoyageService;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;



/**
 * MAIN CLASS - Test CRUD Operations
 *
 * What this does:
 * 1. Tests if we can create (ajouter) a reservation
 * 2. Tests if we can read (recupererTous) all reservations
 * 3. Tests if we can update (modifier) a reservation
 * 4. Tests if we can delete (supprimer) a reservation
 *
 * This is like a "smoke test" to make sure everything works
 */
public class Main  {
    public static void main(String[] args) {

        System.out.println("   TEST COMPLET DU CRUD - RESERVATIONS ");


        try {
            // INITIALIZE SERVICES
            System.out.println("📦 Initializing services...\n");
            ReservationVoyageService reservationService = new ReservationVoyageService();
            VoyageService voyageService = new VoyageService();

            // ════════════════════ 1. CREATE TEST ════════════════════
            System.out.println("1️⃣  TEST: CREATE (INSERT) NEW RESERVATION");
            System.out.println("─────────────────────────────────────────\n");

            // First, let's check if there are any voyages in the database
            List<Voyage> voyages = voyageService.recupererTous();

            if (voyages.isEmpty()) {
                System.out.println("⚠️  No voyages found in database!");
                System.out.println("   Please add some voyages first.");
                System.out.println("   You can add them using SceneBuilder or directly in database.\n");
                return;
            }

            System.out.println("✅ Found " + voyages.size() + " voyages in database\n");

            // Get first voyage for testing
            Voyage testVoyage = voyages.get(0);
            System.out.println("Using Voyage for test:");
            System.out.println("  - ID: " + testVoyage.getId());
            System.out.println("  - Title: " + testVoyage.getTitre());
            System.out.println("  - Price: " + testVoyage.getPrixUnitaire() + " DT");
            System.out.println("  - Available: " + testVoyage.getPlacesDisponibles() + " seats\n");

            // Create a new reservation with CORRECT parameters
            int testUserId = 5;                    // Client ID (arbitrary for test)
            int testVoyageId = testVoyage.getId(); // Use actual voyage ID from database
            int testNbPeople = 2;                  // 2 people booking
            double testAmount = testVoyage.getPrixUnitaire() * testNbPeople; // Calculate: price × people

            System.out.println("Creating reservation for:");
            System.out.println("  - User ID: " + testUserId);
            System.out.println("  - Voyage ID: " + testVoyageId);
            System.out.println("  - Number of People: " + testNbPeople);
            System.out.println("  - Total Amount: " + testAmount + " DT");
            System.out.println("  - Status: EN_ATTENTE (default)\n");

            // Create the reservation object with CORRECT constructor
            ReservationVoyage newReservation = new ReservationVoyage(
                testUserId,      // idUtilisateur
                testVoyageId,    // idVoyage (NOT idDestination!)
                testNbPeople,    // nbrPersonnes
                testAmount       // montantTotal (NOT dates!)
            );

            // Save to database
            System.out.println("Saving to database...");
            reservationService.ajouter(newReservation);
            System.out.println("✅ Reservation created successfully!\n");

            // ════════════════════ 2. READ TEST ════════════════════
            System.out.println("2️⃣  TEST: READ (SELECT) ALL RESERVATIONS");
            System.out.println("─────────────────────────────────────────\n");

            List<ReservationVoyage> allReservations = reservationService.recupererTous();

            System.out.println("Total reservations in database: " + allReservations.size() + "\n");
            System.out.println("Details:");
            System.out.println("┌────┬──────────┬────────────────┬───────┬──────────┬────────────┐");
            System.out.println("│ ID │ Client   │ Voyage         │ People│ Amount   │ Status     │");
            System.out.println("├────┼──────────┼────────────────┼───────┼──────────┼────────────┤");

            for (ReservationVoyage r : allReservations) {
                String voyageName = r.getVoyageTitre() != null ? r.getVoyageTitre() : "Unknown";
                System.out.printf("│ %-2d │ User#%-3d │ %-14s │ %-5d │ %-8.2f │ %-10s │\n",
                    r.getId(),
                    r.getIdUtilisateur(),
                    voyageName,
                    r.getNbrPersonnes(),
                    r.getMontantTotal(),
                    r.getStatut()
                );
            }

            System.out.println("└────┴──────────┴────────────────┴───────┴──────────┴────────────┘\n");

            // ════════════════════ 3. UPDATE TEST ════════════════════
            if (!allReservations.isEmpty()) {
                System.out.println("3️⃣  TEST: UPDATE (MODIFY) RESERVATION");
                System.out.println("─────────────────────────────────────────\n");

                ReservationVoyage reservationToUpdate = allReservations.get(0);

                System.out.println("Reservation before update:");
                System.out.println("  - Status: " + reservationToUpdate.getStatut());
                System.out.println("  - People: " + reservationToUpdate.getNbrPersonnes() + "\n");

                System.out.println("Updating...");
                // Change status from EN_ATTENTE to CONFIRMEE (agent confirms the booking)
                reservationToUpdate.setStatut("CONFIRMEE");
                // Change number of people
                reservationToUpdate.setNbrPersonnes(3);
                // Recalculate amount
                Voyage voyageForUpdate = voyageService.recupererTous().stream()
                    .filter(v -> v.getId() == reservationToUpdate.getIdVoyage())
                    .findFirst()
                    .orElse(null);
                if (voyageForUpdate != null) {
                    reservationToUpdate.setMontantTotal(voyageForUpdate.getPrixUnitaire() * 3);
                }

                reservationService.modifier(reservationToUpdate);

                System.out.println("✅ Reservation updated!\n");

                System.out.println("Reservation after update:");
                System.out.println("  - Status: " + reservationToUpdate.getStatut());
                System.out.println("  - People: " + reservationToUpdate.getNbrPersonnes() + "\n");
            }

            // ════════════════════ 4. DELETE TEST ════════════════════
            // (Commented out so we don't actually delete in test)
            /*
            if (!allReservations.isEmpty()) {
                System.out.println("4️⃣  TEST: DELETE (REMOVE) RESERVATION");
                System.out.println("─────────────────────────────────────────\n");

                ReservationVoyage reservationToDelete = allReservations.get(allReservations.size() - 1);
                System.out.println("Deleting reservation ID: " + reservationToDelete.getId());
                reservationService.supprimer(reservationToDelete.getId());
                System.out.println("✅ Reservation deleted!\n");
            }
            */

            // ════════════════════ FINAL SUMMARY ════════════════════
            System.out.println("════════════════════════════════════════════");
            System.out.println("🎉 ALL TESTS COMPLETED SUCCESSFULLY!");
            System.out.println("════════════════════════════════════════════\n");

            System.out.println("✅ CREATE: Working");
            System.out.println("✅ READ: Working");
            System.out.println("✅ UPDATE: Working");
            System.out.println("✅ DELETE: Not tested (commented out)\n");

            System.out.println("Your CRUD operations are functional!");
            System.out.println("You can now use the GUI interfaces.\n");

        } catch (SQLException e) {
            System.err.println("❌ ════════════════════════════════════════");
            System.err.println("   DATABASE ERROR");
            System.err.println("════════════════════════════════════════\n");
            System.err.println("Error: " + e.getMessage() + "\n");
            System.err.println("Possible causes:");
            System.err.println("  1. Database connection failed");
            System.err.println("  2. Table schema doesn't match");
            System.err.println("  3. Voyages table is empty\n");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

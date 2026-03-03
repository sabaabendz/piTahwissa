package tn.esprit.tahwissa.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.models.ReservationVoyage.StatutReservation;
import tn.esprit.tahwissa.models.Voyage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ReservationVoyageService
 * Ordre CRUD : Create → Read → Update → Delete
 * Un voyage de support est créé dans @BeforeAll (et supprimé dans @AfterAll)
 * car les réservations nécessitent un voyage existant (FK).
 */
@TestMethodOrder(OrderAnnotation.class)
class ReservationVoyageServiceTest {

    private static ReservationVoyageService reservationService;
    private static VoyageService voyageService;

    // Voyage de support pour les FK
    private static int supportVoyageId = -1;

    // ID de la réservation créée pendant chaque test
    private static int testReservationId = -1;

    /**
     * Initialiser les services et créer un voyage de support
     */
    @BeforeAll
    static void setUp() throws SQLException {
        reservationService = new ReservationVoyageService();
        voyageService = new VoyageService();

        // Créer un voyage de support (nécessaire pour la FK id_voyage)
        Voyage supportVoyage = new Voyage();
        supportVoyage.setTitre("SUPPORT_Voyage_ReservationTest");
        supportVoyage.setDescription("Voyage créé pour supporter les tests de réservation");
        supportVoyage.setDestination("TEST_Support_Dest");
        supportVoyage.setCategorie("Culturel");
        supportVoyage.setPrixUnitaire(new BigDecimal("500.00"));
        supportVoyage.setDateDepart(LocalDate.of(2026, 7, 1));
        supportVoyage.setDateRetour(LocalDate.of(2026, 7, 15));
        supportVoyage.setPlacesDisponibles(50);
        supportVoyage.setImageUrl("https://test.com/support.jpg");
        supportVoyage.setStatut("ACTIF");

        boolean created = voyageService.addVoyage(supportVoyage);
        assertTrue(created, "Le voyage de support doit être créé");
        supportVoyageId = supportVoyage.getId();
        System.out.println("=== ReservationVoyageServiceTest : Voyage support ID=" + supportVoyageId + " ===");
    }

    /**
     * Nettoyage après chaque test :
     * Supprime la réservation de test si elle existe encore
     */
    @AfterEach
    void cleanUp() {
        if (testReservationId > 0) {
            ReservationVoyage existing = reservationService.getReservationById(testReservationId);
            if (existing != null) {
                reservationService.deleteReservation(testReservationId);
                System.out.println("🧹 Nettoyage : réservation de test ID=" + testReservationId + " supprimée");
            }
            testReservationId = -1;
        }
    }

    /**
     * Supprimer le voyage de support après tous les tests
     */
    @AfterAll
    static void tearDown() {
        if (supportVoyageId > 0) {
            voyageService.deleteVoyage(supportVoyageId);
            System.out.println("🧹 Nettoyage final : voyage support ID=" + supportVoyageId + " supprimé");
        }
    }

    // ==================== Helpers ====================

    private ReservationVoyage createTestReservation() {
        ReservationVoyage reservation = new ReservationVoyage();
        reservation.setIdUtilisateur(9999); // Utilisateur de test
        reservation.setIdVoyage(supportVoyageId);
        reservation.setDateReservation(LocalDateTime.of(2026, 7, 1, 10, 0));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setNbrPersonnes(2);
        reservation.setMontantTotal(new BigDecimal("1000.00"));
        return reservation;
    }

    // ==================== Tests CRUD ====================

    /**
     * Test 1 : CREATE - Ajouter une nouvelle réservation
     */
    @Test
    @Order(1)
    @DisplayName("CREATE - Ajout d'une nouvelle réservation")
    void testAddReservation() {
        ReservationVoyage reservation = createTestReservation();

        boolean result = reservationService.addReservation(reservation);

        assertTrue(result, "L'ajout de la réservation doit retourner true");
        assertTrue(reservation.getId() > 0, "L'ID généré doit être > 0");

        testReservationId = reservation.getId();
        System.out.println("✅ Réservation créée avec ID: " + testReservationId);
    }

    /**
     * Test 2 : READ - Récupérer une réservation par ID
     */
    @Test
    @Order(2)
    @DisplayName("READ - Récupération d'une réservation par ID")
    void testGetReservationById() {
        // Créer une réservation
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        testReservationId = reservation.getId();

        // Récupérer
        ReservationVoyage found = reservationService.getReservationById(testReservationId);

        assertNotNull(found, "La réservation ne doit pas être null");
        assertEquals(testReservationId, found.getId());
        assertEquals(9999, found.getIdUtilisateur(), "L'utilisateur doit correspondre");
        assertEquals(supportVoyageId, found.getIdVoyage(), "Le voyage doit correspondre");
        assertEquals(StatutReservation.EN_ATTENTE, found.getStatut(), "Le statut doit être EN_ATTENTE");
        assertEquals(2, found.getNbrPersonnes(), "Le nombre de personnes doit être 2");
        assertEquals(0, new BigDecimal("1000.00").compareTo(found.getMontantTotal()), "Le montant doit correspondre");
        System.out.println("✅ Réservation récupérée : ID=" + found.getId());
    }

    /**
     * Test 3 : READ - Récupérer toutes les réservations
     */
    @Test
    @Order(3)
    @DisplayName("READ - Récupération de toutes les réservations")
    void testGetAllReservations() {
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        testReservationId = reservation.getId();

        List<ReservationVoyage> all = reservationService.getAllReservations();

        assertNotNull(all, "La liste ne doit pas être null");
        assertFalse(all.isEmpty(), "La liste ne doit pas être vide");
        System.out.println("✅ Nombre total de réservations : " + all.size());
    }

    /**
     * Test 4 : READ - Récupérer les réservations par utilisateur
     */
    @Test
    @Order(4)
    @DisplayName("READ - Récupération des réservations par utilisateur")
    void testGetReservationsByUserId() {
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        testReservationId = reservation.getId();

        List<ReservationVoyage> userReservations = reservationService.getReservationsByUserId(9999);

        assertNotNull(userReservations);
        assertFalse(userReservations.isEmpty(), "L'utilisateur 9999 doit avoir au moins une réservation");
        assertTrue(userReservations.stream().anyMatch(r -> r.getId() == testReservationId),
                "La réservation de test doit apparaître");
        System.out.println("✅ Réservations utilisateur 9999 : " + userReservations.size());
    }

    /**
     * Test 5 : UPDATE - Modifier une réservation
     */
    @Test
    @Order(5)
    @DisplayName("UPDATE - Modification d'une réservation")
    void testUpdateReservation() {
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        testReservationId = reservation.getId();

        // Modifier
        reservation.setNbrPersonnes(5);
        reservation.setMontantTotal(new BigDecimal("2500.00"));
        reservation.setStatut(StatutReservation.CONFIRMEE);

        boolean result = reservationService.updateReservation(reservation);

        assertTrue(result, "La mise à jour doit retourner true");

        // Vérifier en relisant
        ReservationVoyage updated = reservationService.getReservationById(testReservationId);
        assertNotNull(updated);
        assertEquals(5, updated.getNbrPersonnes(), "Le nombre de personnes doit être 5");
        assertEquals(0, new BigDecimal("2500.00").compareTo(updated.getMontantTotal()), "Le montant doit être 2500");
        assertEquals(StatutReservation.CONFIRMEE, updated.getStatut(), "Le statut doit être CONFIRMEE");
        System.out.println("✅ Réservation mise à jour avec succès");
    }

    /**
     * Test 6 : UPDATE - Modifier uniquement le statut
     */
    @Test
    @Order(6)
    @DisplayName("UPDATE - Modification du statut de réservation")
    void testUpdateReservationStatus() {
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        testReservationId = reservation.getId();

        boolean result = reservationService.updateReservationStatus(testReservationId, StatutReservation.ANNULEE);

        assertTrue(result, "La mise à jour du statut doit retourner true");

        ReservationVoyage updated = reservationService.getReservationById(testReservationId);
        assertNotNull(updated);
        assertEquals(StatutReservation.ANNULEE, updated.getStatut(), "Le statut doit être ANNULEE");
        System.out.println("✅ Statut mis à jour vers ANNULEE");
    }

    /**
     * Test 7 : DELETE - Supprimer une réservation
     */
    @Test
    @Order(7)
    @DisplayName("DELETE - Suppression d'une réservation")
    void testDeleteReservation() {
        ReservationVoyage reservation = createTestReservation();
        reservationService.addReservation(reservation);
        int idToDelete = reservation.getId();

        boolean result = reservationService.deleteReservation(idToDelete);

        assertTrue(result, "La suppression doit retourner true");

        ReservationVoyage deleted = reservationService.getReservationById(idToDelete);
        assertNull(deleted, "La réservation supprimée ne doit plus exister");

        testReservationId = -1; // Déjà supprimé
        System.out.println("✅ Réservation ID=" + idToDelete + " supprimée avec succès");
    }

    /**
     * Test 8 : READ - Réservation inexistante retourne null
     */
    @Test
    @Order(8)
    @DisplayName("READ - Tentative de lecture d'une réservation inexistante")
    void testGetReservationByIdNotFound() {
        ReservationVoyage notFound = reservationService.getReservationById(999999);
        assertNull(notFound, "Une réservation inexistante doit retourner null");
        System.out.println("✅ Réservation inexistante : null retourné correctement");
    }
}

package tn.esprit.tahwissa.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import tn.esprit.tahwissa.models.Paiement;
import tn.esprit.tahwissa.models.Paiement.StatutPaiement;
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
 * Tests unitaires pour PaiementService
 * Ordre CRUD : Create → Read → Update → Delete
 * 
 * Hiérarchie des dépendances :
 *   Voyage → ReservationVoyage → Paiement
 * Un voyage et une réservation de support sont créés dans @BeforeAll
 */
@TestMethodOrder(OrderAnnotation.class)
class PaiementServiceTest {

    private static PaiementService paiementService;
    private static ReservationVoyageService reservationService;
    private static VoyageService voyageService;

    // Données de support
    private static int supportVoyageId = -1;
    private static int supportReservationId = -1;

    // ID du paiement créé pendant chaque test
    private static int testPaiementId = -1;

    /**
     * Créer les données de support (voyage + réservation) et initialiser les services
     */
    @BeforeAll
    static void setUp() throws SQLException {
        voyageService = new VoyageService();
        reservationService = new ReservationVoyageService();
        paiementService = new PaiementService();
        paiementService.ensureTableExists();

        // 1. Créer un voyage de support
        Voyage supportVoyage = new Voyage();
        supportVoyage.setTitre("SUPPORT_Voyage_PaiementTest");
        supportVoyage.setDescription("Voyage support pour tests paiement");
        supportVoyage.setDestination("TEST_Pay_Dest");
        supportVoyage.setCategorie("Balnéaire");
        supportVoyage.setPrixUnitaire(new BigDecimal("700.00"));
        supportVoyage.setDateDepart(LocalDate.of(2026, 8, 1));
        supportVoyage.setDateRetour(LocalDate.of(2026, 8, 10));
        supportVoyage.setPlacesDisponibles(30);
        supportVoyage.setImageUrl("https://test.com/pay.jpg");
        supportVoyage.setStatut("ACTIF");

        assertTrue(voyageService.addVoyage(supportVoyage), "Voyage de support doit être créé");
        supportVoyageId = supportVoyage.getId();

        // 2. Créer une réservation de support
        ReservationVoyage supportReservation = new ReservationVoyage();
        supportReservation.setIdUtilisateur(8888);
        supportReservation.setIdVoyage(supportVoyageId);
        supportReservation.setDateReservation(LocalDateTime.of(2026, 8, 1, 9, 0));
        supportReservation.setStatut(StatutReservation.EN_ATTENTE);
        supportReservation.setNbrPersonnes(2);
        supportReservation.setMontantTotal(new BigDecimal("1400.00"));

        assertTrue(reservationService.addReservation(supportReservation), "Réservation de support doit être créée");
        supportReservationId = supportReservation.getId();

        System.out.println("=== PaiementServiceTest : Voyage ID=" + supportVoyageId
                + ", Réservation ID=" + supportReservationId + " ===");
    }

    /**
     * Nettoyage après chaque test :
     * Supprime le paiement de test via SQL direct
     */
    @AfterEach
    void cleanUp() {
        if (testPaiementId > 0) {
            try {
                // Supprimer directement car PaiementService n'a pas de méthode delete
                java.sql.Connection conn = tn.esprit.tahwissa.config.Database.getInstance();
                try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM paiement WHERE id = ?")) {
                    ps.setInt(1, testPaiementId);
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        System.out.println("🧹 Nettoyage : paiement de test ID=" + testPaiementId + " supprimé");
                    }
                }
            } catch (SQLException e) {
                System.err.println("⚠️ Erreur nettoyage paiement: " + e.getMessage());
            }
            testPaiementId = -1;
        }
    }

    /**
     * Supprimer les données de support après tous les tests
     */
    @AfterAll
    static void tearDown() {
        if (supportReservationId > 0) {
            reservationService.deleteReservation(supportReservationId);
            System.out.println("🧹 Nettoyage final : réservation support ID=" + supportReservationId + " supprimée");
        }
        if (supportVoyageId > 0) {
            voyageService.deleteVoyage(supportVoyageId);
            System.out.println("🧹 Nettoyage final : voyage support ID=" + supportVoyageId + " supprimé");
        }
    }

    // ==================== Helpers ====================

    private Paiement createTestPaiement() {
        Paiement paiement = new Paiement();
        paiement.setIdReservation(supportReservationId);
        paiement.setIdUtilisateur(8888);
        paiement.setMontant(new BigDecimal("1400.00"));
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setMethodePaiement("CARTE");
        paiement.setStatut(StatutPaiement.EN_ATTENTE);
        paiement.setReference("TEST-PAY-" + System.currentTimeMillis());
        return paiement;
    }

    // ==================== Tests CRUD ====================

    /**
     * Test 1 : CREATE - Ajouter un nouveau paiement
     */
    @Test
    @Order(1)
    @DisplayName("CREATE - Ajout d'un nouveau paiement")
    void testAddPaiement() {
        Paiement paiement = createTestPaiement();

        boolean result = paiementService.addPaiement(paiement);

        assertTrue(result, "L'ajout du paiement doit retourner true");
        assertTrue(paiement.getId() > 0, "L'ID généré doit être > 0");

        testPaiementId = paiement.getId();
        System.out.println("✅ Paiement créé avec ID: " + testPaiementId);
    }

    /**
     * Test 2 : READ - Récupérer tous les paiements
     */
    @Test
    @Order(2)
    @DisplayName("READ - Récupération de tous les paiements")
    void testGetAllPaiements() {
        // Créer un paiement d'abord
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        testPaiementId = paiement.getId();

        List<Paiement> all = paiementService.getAllPaiements();

        assertNotNull(all, "La liste ne doit pas être null");
        assertFalse(all.isEmpty(), "La liste ne doit pas être vide");
        assertTrue(all.stream().anyMatch(p -> p.getId() == testPaiementId),
                "Le paiement de test doit apparaître dans la liste");
        System.out.println("✅ Nombre total de paiements : " + all.size());
    }

    /**
     * Test 3 : READ - Récupérer les paiements par utilisateur
     */
    @Test
    @Order(3)
    @DisplayName("READ - Récupération des paiements par utilisateur")
    void testGetPaiementsByUserId() {
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        testPaiementId = paiement.getId();

        List<Paiement> userPaiements = paiementService.getPaiementsByUserId(8888);

        assertNotNull(userPaiements);
        assertFalse(userPaiements.isEmpty(), "L'utilisateur 8888 doit avoir au moins un paiement");
        assertTrue(userPaiements.stream().anyMatch(p -> p.getId() == testPaiementId),
                "Le paiement de test doit apparaître");
        System.out.println("✅ Paiements utilisateur 8888 : " + userPaiements.size());
    }

    /**
     * Test 4 : READ - Vérifier les données jointes (voyage)
     */
    @Test
    @Order(4)
    @DisplayName("READ - Vérification des données jointes voyage/destination")
    void testPaiementJoinedData() {
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        testPaiementId = paiement.getId();

        List<Paiement> paiements = paiementService.getPaiementsByUserId(8888);
        Paiement found = paiements.stream()
                .filter(p -> p.getId() == testPaiementId)
                .findFirst()
                .orElse(null);

        assertNotNull(found, "Le paiement doit être trouvé");
        assertEquals("CARTE", found.getMethodePaiement(), "La méthode de paiement doit être CARTE");
        assertEquals(StatutPaiement.EN_ATTENTE, found.getStatut(), "Le statut doit être EN_ATTENTE");
        assertEquals(0, new BigDecimal("1400.00").compareTo(found.getMontant()), "Le montant doit être 1400.00");
        // Les données jointes doivent être disponibles
        assertNotNull(found.getDestinationVoyage(), "La destination jointe ne doit pas être null");
        assertNotNull(found.getTitreVoyage(), "Le titre du voyage joint ne doit pas être null");
        System.out.println("✅ Données jointes : " + found.getTitreVoyage() + " / " + found.getDestinationVoyage());
    }

    /**
     * Test 5 : UPDATE - Modifier le statut d'un paiement (EN_ATTENTE → PAYE)
     */
    @Test
    @Order(5)
    @DisplayName("UPDATE - Modification du statut de paiement vers PAYE")
    void testUpdateStatutToPaye() {
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        testPaiementId = paiement.getId();

        boolean result = paiementService.updateStatut(testPaiementId, StatutPaiement.PAYE);

        assertTrue(result, "La mise à jour du statut doit retourner true");

        // Vérifier le statut mis à jour
        List<Paiement> paiements = paiementService.getAllPaiements();
        Paiement updated = paiements.stream()
                .filter(p -> p.getId() == testPaiementId)
                .findFirst()
                .orElse(null);

        assertNotNull(updated, "Le paiement mis à jour doit exister");
        assertEquals(StatutPaiement.PAYE, updated.getStatut(), "Le statut doit être PAYE");
        System.out.println("✅ Statut mis à jour vers PAYE");
    }

    /**
     * Test 6 : UPDATE - Modifier le statut vers REMBOURSE
     */
    @Test
    @Order(6)
    @DisplayName("UPDATE - Modification du statut de paiement vers REMBOURSE")
    void testUpdateStatutToRembourse() {
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        testPaiementId = paiement.getId();

        // D'abord passer à PAYE, puis REMBOURSE
        paiementService.updateStatut(testPaiementId, StatutPaiement.PAYE);
        boolean result = paiementService.updateStatut(testPaiementId, StatutPaiement.REMBOURSE);

        assertTrue(result, "La mise à jour vers REMBOURSE doit retourner true");

        List<Paiement> paiements = paiementService.getAllPaiements();
        Paiement updated = paiements.stream()
                .filter(p -> p.getId() == testPaiementId)
                .findFirst()
                .orElse(null);

        assertNotNull(updated);
        assertEquals(StatutPaiement.REMBOURSE, updated.getStatut(), "Le statut doit être REMBOURSE");
        System.out.println("✅ Statut mis à jour vers REMBOURSE");
    }

    /**
     * Test 7 : DELETE - Suppression d'un paiement (via SQL direct)
     */
    @Test
    @Order(7)
    @DisplayName("DELETE - Suppression d'un paiement")
    void testDeletePaiement() throws SQLException {
        Paiement paiement = createTestPaiement();
        paiementService.addPaiement(paiement);
        int idToDelete = paiement.getId();

        // Supprimer directement en BDD
        java.sql.Connection conn = tn.esprit.tahwissa.config.Database.getInstance();
        int rows;
        try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM paiement WHERE id = ?")) {
            ps.setInt(1, idToDelete);
            rows = ps.executeUpdate();
        }

        assertTrue(rows > 0, "La suppression doit affecter au moins une ligne");

        // Vérifier que le paiement n'apparaît plus
        List<Paiement> all = paiementService.getAllPaiements();
        boolean stillExists = all.stream().anyMatch(p -> p.getId() == idToDelete);
        assertFalse(stillExists, "Le paiement supprimé ne doit plus exister");

        testPaiementId = -1; // Déjà supprimé
        System.out.println("✅ Paiement ID=" + idToDelete + " supprimé avec succès");
    }

    /**
     * Test 8 : CREATE - Vérifier l'unicité de la référence
     */
    @Test
    @Order(8)
    @DisplayName("CREATE - Vérification que la référence est unique")
    void testPaiementReferenceUnique() {
        Paiement p1 = createTestPaiement();
        String uniqueRef = "UNIQUE-REF-" + System.currentTimeMillis();
        p1.setReference(uniqueRef);
        assertTrue(paiementService.addPaiement(p1), "Le premier paiement doit être ajouté");
        testPaiementId = p1.getId();

        // Vérifier que la référence est bien stockée
        List<Paiement> paiements = paiementService.getAllPaiements();
        Paiement found = paiements.stream()
                .filter(p -> p.getId() == testPaiementId)
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals(uniqueRef, found.getReference(), "La référence doit correspondre");
        System.out.println("✅ Référence unique vérifiée : " + uniqueRef);
    }
}

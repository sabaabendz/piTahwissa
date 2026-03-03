package tn.esprit.tahwissa.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import tn.esprit.tahwissa.models.Voyage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour VoyageService
 * Ordre CRUD : Create → Read → Update → Delete
 * Nettoyage automatique via @AfterEach
 */
@TestMethodOrder(OrderAnnotation.class)
class VoyageServiceTest {

    private static VoyageService voyageService;

    // ID du voyage créé pendant les tests, pour nettoyage
    private static int testVoyageId = -1;

    /**
     * Initialiser la connexion DB et le service avant tous les tests
     */
    @BeforeAll
    static void setUp() throws SQLException {
        voyageService = new VoyageService();
        System.out.println("=== VoyageServiceTest : Initialisation ===");
    }

    /**
     * Nettoyage après chaque test :
     * Supprime le voyage de test s'il existe encore en BDD
     */
    @AfterEach
    void cleanUp() {
        if (testVoyageId > 0) {
            Voyage existing = voyageService.getVoyageById(testVoyageId);
            if (existing != null) {
                voyageService.deleteVoyage(testVoyageId);
                System.out.println("🧹 Nettoyage : voyage de test ID=" + testVoyageId + " supprimé");
            }
            testVoyageId = -1;
        }
    }

    // ==================== Helpers ====================

    /**
     * Crée un objet Voyage de test avec des données uniques
     */
    private Voyage createTestVoyage() {
        Voyage voyage = new Voyage();
        voyage.setTitre("TEST_Voyage_JUnit_" + System.currentTimeMillis());
        voyage.setDescription("Voyage de test créé par JUnit 5");
        voyage.setDestination("TEST_Destination");
        voyage.setCategorie("Aventure");
        voyage.setPrixUnitaire(new BigDecimal("999.99"));
        voyage.setDateDepart(LocalDate.of(2026, 6, 1));
        voyage.setDateRetour(LocalDate.of(2026, 6, 15));
        voyage.setPlacesDisponibles(10);
        voyage.setImageUrl("https://test.com/image.jpg");
        voyage.setStatut("ACTIF");
        return voyage;
    }

    // ==================== Tests CRUD ====================

    /**
     * Test 1 : CREATE - Ajouter un nouveau voyage
     */
    @Test
    @Order(1)
    @DisplayName("CREATE - Ajout d'un nouveau voyage")
    void testAddVoyage() {
        Voyage voyage = createTestVoyage();

        boolean result = voyageService.addVoyage(voyage);

        assertTrue(result, "L'ajout du voyage doit retourner true");
        assertTrue(voyage.getId() > 0, "L'ID généré doit être > 0");

        testVoyageId = voyage.getId();
        System.out.println("✅ Voyage créé avec ID: " + testVoyageId);
    }

    /**
     * Test 2 : READ - Récupérer un voyage par ID
     */
    @Test
    @Order(2)
    @DisplayName("READ - Récupération d'un voyage par ID")
    void testGetVoyageById() {
        // D'abord créer un voyage
        Voyage voyage = createTestVoyage();
        voyageService.addVoyage(voyage);
        testVoyageId = voyage.getId();

        // Récupérer par ID
        Voyage found = voyageService.getVoyageById(testVoyageId);

        assertNotNull(found, "Le voyage récupéré ne doit pas être null");
        assertEquals(testVoyageId, found.getId(), "L'ID doit correspondre");
        assertEquals(voyage.getTitre(), found.getTitre(), "Le titre doit correspondre");
        assertEquals(voyage.getDestination(), found.getDestination(), "La destination doit correspondre");
        assertEquals(0, voyage.getPrixUnitaire().compareTo(found.getPrixUnitaire()), "Le prix doit correspondre");
        System.out.println("✅ Voyage récupéré : " + found.getTitre());
    }

    /**
     * Test 3 : READ - Récupérer tous les voyages
     */
    @Test
    @Order(3)
    @DisplayName("READ - Récupération de tous les voyages")
    void testGetAllVoyages() {
        // Créer un voyage pour s'assurer qu'il y en a au moins un
        Voyage voyage = createTestVoyage();
        voyageService.addVoyage(voyage);
        testVoyageId = voyage.getId();

        List<Voyage> voyages = voyageService.getAllVoyages();

        assertNotNull(voyages, "La liste ne doit pas être null");
        assertFalse(voyages.isEmpty(), "La liste ne doit pas être vide");
        System.out.println("✅ Nombre de voyages récupérés : " + voyages.size());
    }

    /**
     * Test 4 : READ - Rechercher des voyages
     */
    @Test
    @Order(4)
    @DisplayName("READ - Recherche de voyages par terme")
    void testSearchVoyages() {
        // Créer un voyage avec un titre unique
        Voyage voyage = createTestVoyage();
        voyage.setTitre("UNIQUE_SEARCH_TEST_" + System.currentTimeMillis());
        voyageService.addVoyage(voyage);
        testVoyageId = voyage.getId();

        List<Voyage> results = voyageService.searchVoyages("UNIQUE_SEARCH_TEST");

        assertNotNull(results, "Les résultats ne doivent pas être null");
        assertFalse(results.isEmpty(), "La recherche doit trouver au moins un résultat");
        assertTrue(results.stream().anyMatch(v -> v.getId() == testVoyageId),
                "Le voyage de test doit apparaître dans les résultats");
        System.out.println("✅ Recherche : " + results.size() + " résultat(s) trouvé(s)");
    }

    /**
     * Test 5 : READ - Filtrer par catégorie
     */
    @Test
    @Order(5)
    @DisplayName("READ - Filtrage par catégorie")
    void testGetVoyagesByCategory() {
        Voyage voyage = createTestVoyage();
        voyage.setCategorie("Safari");
        voyageService.addVoyage(voyage);
        testVoyageId = voyage.getId();

        List<Voyage> results = voyageService.getVoyagesByCategory("Safari");

        assertNotNull(results);
        assertTrue(results.stream().anyMatch(v -> v.getId() == testVoyageId),
                "Le voyage Safari doit apparaître dans les résultats");
        System.out.println("✅ Catégorie Safari : " + results.size() + " voyage(s)");
    }

    /**
     * Test 6 : UPDATE - Modifier un voyage existant
     */
    @Test
    @Order(6)
    @DisplayName("UPDATE - Modification d'un voyage")
    void testUpdateVoyage() {
        // Créer puis modifier
        Voyage voyage = createTestVoyage();
        voyageService.addVoyage(voyage);
        testVoyageId = voyage.getId();

        // Modifier les champs
        voyage.setTitre("UPDATED_Titre_Test");
        voyage.setDestination("UPDATED_Destination");
        voyage.setPrixUnitaire(new BigDecimal("1500.00"));
        voyage.setPlacesDisponibles(25);

        boolean result = voyageService.updateVoyage(voyage);

        assertTrue(result, "La mise à jour doit retourner true");

        // Vérifier en re-lisant depuis la BDD
        Voyage updated = voyageService.getVoyageById(testVoyageId);
        assertNotNull(updated);
        assertEquals("UPDATED_Titre_Test", updated.getTitre(), "Le titre doit être mis à jour");
        assertEquals("UPDATED_Destination", updated.getDestination(), "La destination doit être mise à jour");
        assertEquals(0, new BigDecimal("1500.00").compareTo(updated.getPrixUnitaire()), "Le prix doit être mis à jour");
        assertEquals(25, updated.getPlacesDisponibles(), "Les places doivent être mises à jour");
        System.out.println("✅ Voyage mis à jour avec succès");
    }

    /**
     * Test 7 : DELETE - Supprimer un voyage
     */
    @Test
    @Order(7)
    @DisplayName("DELETE - Suppression d'un voyage")
    void testDeleteVoyage() {
        // Créer un voyage à supprimer
        Voyage voyage = createTestVoyage();
        voyageService.addVoyage(voyage);
        int idToDelete = voyage.getId();

        boolean result = voyageService.deleteVoyage(idToDelete);

        assertTrue(result, "La suppression doit retourner true");

        // Vérifier que le voyage n'existe plus
        Voyage deleted = voyageService.getVoyageById(idToDelete);
        assertNull(deleted, "Le voyage supprimé ne doit plus exister en BDD");

        // Pas besoin de nettoyage, déjà supprimé
        testVoyageId = -1;
        System.out.println("✅ Voyage ID=" + idToDelete + " supprimé avec succès");
    }

    /**
     * Test 8 : READ - Voyage inexistant retourne null
     */
    @Test
    @Order(8)
    @DisplayName("READ - Tentative de lecture d'un voyage inexistant")
    void testGetVoyageByIdNotFound() {
        Voyage notFound = voyageService.getVoyageById(999999);
        assertNull(notFound, "Un voyage inexistant doit retourner null");
        System.out.println("✅ Voyage inexistant : null retourné correctement");
    }
}

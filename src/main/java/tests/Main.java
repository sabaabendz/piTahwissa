package tests;

import entites.Reservation_transport;
import entites.Transport;
import entites.Utilisateur;
import services.Reservation_transportService;
import services.TransportService;
import services.UtilisateurService;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TransportService transportService = new TransportService();
        Reservation_transportService reservationService = new Reservation_transportService();
        UtilisateurService utilisateurService = new UtilisateurService();

        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1. Gestion Transport");
            System.out.println("2. Gestion Réservation Transport");
            System.out.println("3. Gestion Utilisateur");
            System.out.println("4. Quitter");
            System.out.print("Choisissez une option : ");

            int choix = readInt(sc, "");

            try {
                switch (choix) {
                    case 1 -> menuTransport(sc, transportService);
                    case 2 -> menuReservation(sc, reservationService);
                    case 3 -> menuUtilisateur(sc, utilisateurService);
                    case 4 -> {
                        exit = true;
                        System.out.println("Au revoir !");
                    }
                    default -> System.out.println("Option invalide !");
                }
            } catch (SQLException e) {
                System.out.println("Erreur SQL : " + e.getMessage());
            }
        }

        sc.close();
    }

    // ===================== MENU TRANSPORT =====================
    private static void menuTransport(Scanner sc, TransportService transportService) throws SQLException {
        System.out.println("\n--- GESTION TRANSPORT ---");
        System.out.println("1. Ajouter");
        System.out.println("2. Modifier");
        System.out.println("3. Supprimer");
        System.out.println("4. Afficher tous");
        System.out.print("Choisissez : ");
        int choix = readInt(sc, "");

        switch (choix) {
            case 1 -> {
                Transport t = saisirTransport(sc, false);
                transportService.ajouterTransport(t);
                System.out.println("✅ Transport ajouté !");
            }
            case 2 -> {
                Transport t = saisirTransport(sc, true);
                transportService.modifierTransport(t);
                System.out.println("✅ Transport modifié !");
            }
            case 3 -> {
                int id = readInt(sc, "ID transport à supprimer : ");
                transportService.supprimerTransport(id);
                System.out.println("✅ Transport supprimé !");
            }
            case 4 -> {
                List<Transport> list = transportService.recupererTransport();
                if (list.isEmpty()) System.out.println("Aucun transport trouvé.");
                else list.forEach(System.out::println);
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MENU RESERVATION =====================
    private static void menuReservation(Scanner sc, Reservation_transportService reservationService) throws SQLException {
        System.out.println("\n--- GESTION RESERVATION TRANSPORT ---");
        System.out.println("1. Ajouter");
        System.out.println("2. Modifier");
        System.out.println("3. Supprimer");
        System.out.println("4. Afficher tous");
        System.out.print("Choisissez : ");
        int choix = readInt(sc, "");

        switch (choix) {
            case 1 -> {
                Reservation_transport r = saisirReservation(sc, false);
                reservationService.ajouterReservation(r);
                System.out.println("✅ Réservation ajoutée !");
            }
            case 2 -> {
                Reservation_transport r = saisirReservation(sc, true);
                reservationService.modifierReservation(r);
                System.out.println("✅ Réservation modifiée !");
            }
            case 3 -> {
                int id = readInt(sc, "ID réservation à supprimer : ");
                Reservation_transport r = new Reservation_transport();
                r.setIdReservation(id);
                reservationService.supprimerReservation(r);
                System.out.println("✅ Réservation supprimée !");
            }
            case 4 -> {
                List<Reservation_transport> list = reservationService.recupererReservations();
                if (list.isEmpty()) System.out.println("Aucune réservation trouvée.");
                else list.forEach(System.out::println);
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== MENU UTILISATEUR =====================
    private static void menuUtilisateur(Scanner sc, UtilisateurService utilisateurService) throws SQLException {
        System.out.println("\n--- GESTION UTILISATEUR ---");
        System.out.println("1. Ajouter");
        System.out.println("2. Modifier");
        System.out.println("3. Supprimer");
        System.out.println("4. Afficher tous");
        System.out.print("Choisissez : ");
        int choix = readInt(sc, "");

        switch (choix) {
            case 1 -> {
                Utilisateur u = saisirUtilisateur(sc, false);
                utilisateurService.ajouterUtilisateur(u);
                System.out.println("✅ Utilisateur ajouté !");
            }
            case 2 -> {
                Utilisateur u = saisirUtilisateur(sc, true);
                utilisateurService.modifierUtilisateur(u);
                System.out.println("✅ Utilisateur modifié !");
            }
            case 3 -> {
                int id = readInt(sc, "ID utilisateur à supprimer : ");
                utilisateurService.supprimerUtilisateur(id);
                System.out.println("✅ Utilisateur supprimé !");
            }
            case 4 -> {
                List<Utilisateur> list = utilisateurService.recupererUtilisateurs();
                if (list.isEmpty()) System.out.println("Aucun utilisateur trouvé.");
                else list.forEach(System.out::println);
            }
            default -> System.out.println("Option invalide !");
        }
    }

    // ===================== SAISIE TRANSPORT =====================
    private static Transport saisirTransport(Scanner sc, boolean withId) {
        Transport t = new Transport();

        if (withId) {
            int id = readInt(sc, "ID transport : ");
            t.setIdTransport(id);
        }

        System.out.print("Type transport (Bus/Train/Avion/VTC...) : ");
        t.setTypeTransport(sc.nextLine());

        System.out.print("Ville départ : ");
        t.setVilleDepart(sc.nextLine());

        System.out.print("Ville arrivée : ");
        t.setVilleArrivee(sc.nextLine());

        t.setDateDepart(readDate(sc, "Date départ (YYYY-MM-DD) : "));
        t.setHeureDepart(readTime(sc, "Heure départ (HH:MM:SS) : "));

        t.setDuree(readInt(sc, "Durée (minutes) : "));
        t.setPrix(readDouble(sc, "Prix : "));
        t.setNbPlaces(readInt(sc, "Nombre de places : "));

        return t;
    }

    // ===================== SAISIE RESERVATION =====================
    private static Reservation_transport saisirReservation(Scanner sc, boolean withId) {
        Reservation_transport r = new Reservation_transport();

        if (withId) {
            int id = readInt(sc, "ID réservation : ");
            r.setIdReservation(id);
        }

        r.setDateReservation(readDate(sc, "Date réservation (YYYY-MM-DD) : "));
        r.setNbPlacesReservees(readInt(sc, "Places réservées : "));

        System.out.print("Statut (EN_ATTENTE/CONFIRMEE/ANNULEE) : ");
        r.setStatut(sc.nextLine().toUpperCase());

        r.setIdTransport(readInt(sc, "ID transport : "));
        r.setIdUser(readInt(sc, "ID utilisateur : "));

        return r;
    }

    // ===================== SAISIE UTILISATEUR =====================
    private static Utilisateur saisirUtilisateur(Scanner sc, boolean withId) {
        Utilisateur u = new Utilisateur();

        if (withId) {
            u.setIdUser(readInt(sc, "ID user : "));
        }

        System.out.print("Nom : ");
        u.setNom(sc.nextLine());

        System.out.print("Email : ");
        u.setEmail(sc.nextLine());

        System.out.print("Mot de passe : ");
        u.setMotDePasse(sc.nextLine());

        System.out.print("Role (USER/ADMIN/AGENT) : ");
        u.setRole(sc.nextLine().toUpperCase());

        return u;
    }

    // ===================== INPUT HELPERS =====================
    private static int readInt(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Erreur : veuillez entrer un entier valide.");
            }
        }
    }

    private static double readDouble(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Double.parseDouble(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Erreur : veuillez entrer un nombre valide.");
            }
        }
    }

    private static Date readDate(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Date.valueOf(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Format invalide. Exemple : 2026-02-10");
            }
        }
    }

    private static Time readTime(Scanner sc, String message) {
        while (true) {
            try {
                System.out.print(message);
                return Time.valueOf(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Format invalide. Exemple : 08:30:00");
            }
        }
    }
}


-- ═══════════════════════════════════════════════════════════
-- TAHWISSA - BASE DE DONNÉES COMPLÈTE INTÉGRÉE
--
-- Modules:
-- 1. Gestion de Voyages (voyage, reservationvoyage)
-- 2. Gestion d'Événements (evenement, utilisateur, reservation_evenement, reclamation)
-- 3. Gestion de Destinations (destination, point_interet)
-- ═══════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS gestionreservation CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestionreservation;

-- ═══════════════════════════════════════════════════════════
-- MODULE 1: GESTION DE VOYAGES
-- Tables: voyage, reservationvoyage
-- ═══════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS `voyage` (
                                        `id` int(100) NOT NULL AUTO_INCREMENT,
    `titre` varchar(255) NOT NULL,
    `description` text DEFAULT NULL,
    `destination` varchar(255) NOT NULL,
    `categorie` varchar(100) DEFAULT NULL,
    `prix_unitaire` decimal(10,2) NOT NULL,
    `date_depart` date DEFAULT NULL,
    `date_retour` date DEFAULT NULL,
    `places_disponibles` int(11) NOT NULL DEFAULT 0,
    `image_url` varchar(255) DEFAULT NULL,
    `statut` varchar(20) DEFAULT 'ACTIF',
    `created_at` datetime DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `reservationvoyage` (
                                                   `id` int(100) NOT NULL AUTO_INCREMENT,
    `idUtilisateur` int(100) NOT NULL,
    `id_voyage` int(11) NOT NULL,
    `date_reservation` datetime DEFAULT current_timestamp(),
    `statut` enum('EN_ATTENTE','CONFIRMEE','ANNULEE','TERMINEE') NOT NULL DEFAULT 'EN_ATTENTE',
    `nbrPersonnes` int(100) NOT NULL DEFAULT 1,
    `montantTotal` decimal(10,2) NOT NULL DEFAULT 10.20,
    `dateCreation` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`id`),
    KEY `idUtilisateur` (`idUtilisateur`),
    KEY `fk_reservation_voyage` (`id_voyage`),
    CONSTRAINT `fk_reservation_voyage` FOREIGN KEY (`id_voyage`) REFERENCES `voyage` (`id`) ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════════
-- MODULE 2: GESTION D'ÉVÉNEMENTS
-- Tables: utilisateur, evenement, reservation_evenement, reclamation
-- ═══════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS `utilisateur` (
                                             `id_user` int(11) NOT NULL AUTO_INCREMENT,
    `nom` varchar(100) NOT NULL,
    `email` varchar(150) NOT NULL,
    `mot_de_passe` varchar(255) NOT NULL,
    `role` varchar(50) DEFAULT 'USER',
    PRIMARY KEY (`id_user`),
    UNIQUE KEY `email` (`email`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `evenement` (
                                           `id_evenement` int(11) NOT NULL AUTO_INCREMENT,
    `titre` varchar(150) NOT NULL,
    `description` text DEFAULT NULL,
    `lieu` varchar(120) NOT NULL,
    `date_event` date NOT NULL,
    `heure_event` time NOT NULL,
    `prix` double NOT NULL,
    `nb_places` int(11) NOT NULL,
    `categorie` varchar(80) DEFAULT NULL,
    `statut` varchar(50) DEFAULT 'DISPONIBLE',
    PRIMARY KEY (`id_evenement`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `reservation_evenement` (
                                                       `id_reservation` int(11) NOT NULL AUTO_INCREMENT,
    `date_reservation` date NOT NULL,
    `nb_places_reservees` int(11) NOT NULL,
    `statut` varchar(50) DEFAULT 'EN_ATTENTE',
    `id_evenement` int(11) DEFAULT NULL,
    `id_user` int(11) DEFAULT NULL,
    PRIMARY KEY (`id_reservation`),
    KEY `id_evenement` (`id_evenement`),
    KEY `id_user` (`id_user`),
    CONSTRAINT `fk_reservation_evenement_evenement` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`id_evenement`) ON DELETE CASCADE,
    CONSTRAINT `fk_reservation_evenement_user` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `reclamation` (
                                             `id_reclamation` int(11) NOT NULL AUTO_INCREMENT,
    `titre` varchar(150) NOT NULL,
    `description` text NOT NULL,
    `type` varchar(80) DEFAULT NULL,
    `statut` varchar(50) DEFAULT 'EN_ATTENTE',
    `date_creation` datetime DEFAULT CURRENT_TIMESTAMP,
    `id_user` int(11) DEFAULT NULL,
    PRIMARY KEY (`id_reclamation`),
    KEY `id_user` (`id_user`),
    CONSTRAINT `fk_reclamation_user` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════════
-- MODULE 3: GESTION DE DESTINATIONS
-- Tables: destination, point_interet
-- ═══════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS `destination` (
                                             `id_destination` int(11) NOT NULL AUTO_INCREMENT,
    `nom` varchar(100) NOT NULL,
    `pays` varchar(100) NOT NULL,
    `ville` varchar(100) DEFAULT NULL,
    `description` text DEFAULT NULL,
    `image_url` varchar(255) DEFAULT NULL,
    `latitude` decimal(10,8) DEFAULT NULL,
    `longitude` decimal(11,8) DEFAULT NULL,
    `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`id_destination`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `point_interet` (
                                               `id_point_interet` int(11) NOT NULL AUTO_INCREMENT,
    `nom` varchar(100) NOT NULL,
    `type` varchar(50) NOT NULL,
    `description` text DEFAULT NULL,
    `image_url` varchar(255) DEFAULT NULL,
    `destination_id` int(11) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
    `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    PRIMARY KEY (`id_point_interet`),
    KEY `destination_id` (`destination_id`),
    CONSTRAINT `point_interet_ibfk_1` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`id_destination`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════════
-- DONNÉES DE TEST - MODULE VOYAGES
-- ═══════════════════════════════════════════════════════════

INSERT INTO `voyage` (`titre`, `description`, `destination`, `categorie`, `prix_unitaire`, `date_depart`, `date_retour`, `places_disponibles`, `image_url`, `statut`) VALUES
                                                                                                                                                                          ('Séjour à Djerba', 'Profitez des plages paradisiaques et du soleil.', 'Djerba', 'Détente', 450.00, '2026-07-10', '2026-07-17', 15, '/images/djerba.jpg', 'ACTIF'),
                                                                                                                                                                          ('Circuit Andalucia', 'Découvrez les villes historiques du sud de l\'Espagne.', 'Espagne', 'Culturel', 890.00, '2026-09-05', '2026-09-12', 8, '/images/andalucia.jpg', 'ACTIF'),
('Aventure en Sardaigne', 'Randonnées et criques secrètes.', 'Sardaigne', 'Aventure', 650.00, '2026-08-20', '2026-08-27', 10, '/images/sardaigne.jpg', 'ACTIF'),
('Week-end à Paris', 'Séjour romantique avec croisière sur la Seine.', 'Paris', 'Romantique', 299.00, '2026-06-15', '2026-06-18', 20, '/images/paris.jpg', 'ACTIF'),
('Safari au Kenya', 'Rencontrez la faune sauvage dans son habitat naturel.', 'Kenya', 'Aventure', 1500.00, '2026-10-01', '2026-10-10', 5, '/images/kenya.jpg', 'ACTIF');

INSERT INTO `reservationvoyage` (`idUtilisateur`, `id_voyage`, `statut`, `nbrPersonnes`, `montantTotal`) VALUES
(1, 1, 'CONFIRMEE', 2, 900.00),
(2, 2, 'EN_ATTENTE', 1, 890.00),
(1, 3, 'CONFIRMEE', 4, 2600.00),
(3, 4, 'ANNULEE', 2, 598.00),
(2, 5, 'EN_ATTENTE', 3, 4500.00);

-- ═══════════════════════════════════════════════════════════
-- DONNÉES DE TEST - MODULE ÉVÉNEMENTS
-- ═══════════════════════════════════════════════════════════

INSERT INTO `utilisateur` (`nom`, `email`, `mot_de_passe`, `role`) VALUES
('Administrateur', 'admin@tahwissa.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN'),
('Ahmed Ben Ali', 'ahmed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
('Sarah Meziani', 'sarah@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
('Mohamed Karoui', 'mohamed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER');

INSERT INTO `evenement` (`titre`, `description`, `lieu`, `date_event`, `heure_event`, `prix`, `nb_places`, `categorie`, `statut`) VALUES
('Concert Live Jazz', 'Soirée jazz exceptionnelle avec les meilleurs artistes', 'Carthage Theatre', '2026-03-20', '20:00:00', 50.00, 200, 'Musique', 'DISPONIBLE'),
('Festival du Film', 'Projection de films internationaux', 'Cinema Rio', '2026-04-15', '18:00:00', 30.00, 150, 'Culture', 'DISPONIBLE'),
('Conférence Tech', 'Innovation et technologies du futur', 'Centre de conférences', '2026-05-10', '09:00:00', 0.00, 300, 'Technologie', 'DISPONIBLE'),
('Marathon de Tunis', 'Course de 42km à travers la ville', 'Avenue Habib Bourguiba', '2026-06-01', '07:00:00', 25.00, 500, 'Sport', 'DISPONIBLE'),
('Exposition d\'Art', 'Œuvres contemporaines d\'artistes locaux', 'Galerie El Teatro', '2026-03-25', '10:00:00', 15.00, 100, 'Art', 'DISPONIBLE');

INSERT INTO `reservation_evenement` (`date_reservation`, `nb_places_reservees`, `statut`, `id_evenement`, `id_user`) VALUES
('2026-02-15', 2, 'CONFIRMEE', 1, 2),
('2026-02-14', 4, 'EN_ATTENTE', 2, 3),
('2026-02-13', 3, 'CONFIRMEE', 3, 4);

INSERT INTO `reclamation` (`titre`, `description`, `type`, `statut`, `date_creation`, `id_user`) VALUES
('Problème de paiement', 'Le paiement n\'a pas été traité correctement', 'Technique', 'EN_ATTENTE', NOW(), 2),
                                                                                                                                                                          ('Annulation de réservation', 'Je souhaite annuler ma réservation pour le concert', 'Service Client', 'EN_COURS', NOW(), 3),
                                                                                                                                                                          ('Information manquante', 'Les détails de l\'événement ne sont pas clairs', 'Information', 'TRAITEE', NOW(), 4);

-- ═══════════════════════════════════════════════════════════
-- DONNÉES DE TEST - MODULE DESTINATIONS
-- ═══════════════════════════════════════════════════════════

INSERT INTO `destination` (`nom`, `pays`, `ville`, `description`, `image_url`, `latitude`, `longitude`) VALUES
('Djerba', 'Tunisie', 'djerbahood', 'Île paradisiaque connue pour ses plages et son patrimoine culturel', 'djerba.jpg', 33.80760000, 10.84510000),
('Sidi Bou Said', 'Tunisie', 'Tunis', 'Village pittoresque aux maisons bleues et blanches', 'sidibousaid.jpg', 36.86860000, 10.34110000);

INSERT INTO `point_interet` (`nom`, `type`, `description`, `image_url`, `destination_id`) VALUES
('Plage de Sidi Mahrez', 'plage', 'Magnifique plage de sable blanc', 'plage_mahrez.jpg', 1),
('Synagogue de la Ghriba', 'monument', 'Synagogue historique et lieu de pèlerinage', 'ghriba.jpg', 1),
('Musée de Guellala', 'musée', 'Musée dédié aux traditions de Djerba', 'guellala.jpg', 1),
('Café des Délices', 'restaurant', 'Café emblématique avec vue sur la mer', 'cafe_delices.jpg', 2);
```

---
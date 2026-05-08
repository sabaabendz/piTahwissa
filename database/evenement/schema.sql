-- Script SQL complet pour Tahwissa
-- Base de données : evenementbd

-- Création de la base (si elle n'existe pas)
CREATE DATABASE IF NOT EXISTS evenementbd CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE evenementbd;

-- Table utilisateur
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id_user` int(11) NOT NULL AUTO_INCREMENT,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `role` varchar(50) DEFAULT 'USER',
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table evenement
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table reservation_evenement
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
  CONSTRAINT `reservation_evenement_ibfk_1` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`id_evenement`) ON DELETE CASCADE,
  CONSTRAINT `reservation_evenement_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table reclamation (nouvelle table pour les réclamations)
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
  CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Insertion de données de test

-- Utilisateurs (mot de passe: "password" hashé en SHA-256 simple pour la démo)
INSERT INTO `utilisateur` (`nom`, `email`, `mot_de_passe`, `role`) VALUES
('Administrateur', 'admin@tahwissa.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN'),
('Ahmed Ben Ali', 'ahmed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
('Sarah Meziani', 'sarah@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
('Mohamed Karoui', 'mohamed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER');

-- Événements
INSERT INTO `evenement` (`titre`, `description`, `lieu`, `date_event`, `heure_event`, `prix`, `nb_places`, `categorie`, `statut`) VALUES
('Concert Live Jazz', 'Soirée jazz exceptionnelle avec les meilleurs artistes', 'Carthage Theatre', '2026-03-20', '20:00:00', 50.00, 200, 'Musique', 'DISPONIBLE'),
('Festival du Film', 'Projection de films internationaux', 'Cinema Rio', '2026-04-15', '18:00:00', 30.00, 150, 'Culture', 'DISPONIBLE'),
('Conférence Tech', 'Innovation et technologies du futur', 'Centre de conférences', '2026-05-10', '09:00:00', 0.00, 300, 'Technologie', 'DISPONIBLE'),
('Marathon de Tunis', 'Course de 42km à travers la ville', 'Avenue Habib Bourguiba', '2026-06-01', '07:00:00', 25.00, 500, 'Sport', 'DISPONIBLE'),
('Exposition d\'Art', 'Œuvres contemporaines d\'artistes locaux', 'Galerie El Teatro', '2026-03-25', '10:00:00', 15.00, 100, 'Art', 'DISPONIBLE');

-- Réservations
INSERT INTO `reservation_evenement` (`date_reservation`, `nb_places_reservees`, `statut`, `id_evenement`, `id_user`) VALUES
('2026-02-15', 2, 'CONFIRMEE', 1, 2),
('2026-02-14', 4, 'EN_ATTENTE', 2, 3),
('2026-02-13', 3, 'CONFIRMEE', 3, 4);

-- Réclamations
INSERT INTO `reclamation` (`titre`, `description`, `type`, `statut`, `date_creation`, `id_user`) VALUES
('Problème de paiement', 'Le paiement n\'a pas été traité correctement', 'Technique', 'EN_ATTENTE', NOW(), 2),
('Annulation de réservation', 'Je souhaite annuler ma réservation pour le concert', 'Service Client', 'EN_COURS', NOW(), 3),
('Information manquante', 'Les détails de l\'événement ne sont pas clairs', 'Information', 'TRAITEE', NOW(), 4);

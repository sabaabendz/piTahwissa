-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : jeu. 02 avr. 2026 à 22:08
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `gestionreservation`
--

-- --------------------------------------------------------

--
-- Structure de la table `destination`
--

CREATE TABLE `destination` (
  `id_destination` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `pays` varchar(100) NOT NULL,
  `ville` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `destination`
--

INSERT INTO `destination` (`id_destination`, `nom`, `pays`, `ville`, `description`, `image_url`, `latitude`, `longitude`, `created_at`, `updated_at`) VALUES
(1, 'Djerba', 'Tunisie', 'djerbahood', 'Île paradisiaque connue pour ses plages et son patrimoine culturel', 'djerba.jpg', 33.80760000, 10.84510000, '2026-02-16 13:16:01', '2026-02-16 13:16:01'),
(2, 'Sidi Bou Said', 'Tunisie', 'Tunis', 'Village pittoresque aux maisons bleues et blanches', 'sidibousaid.jpg', 36.86860000, 10.34110000, '2026-02-16 13:16:01', '2026-02-16 13:16:01');

-- --------------------------------------------------------

--
-- Structure de la table `evenement`
--

CREATE TABLE `evenement` (
  `id_evenement` int(11) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `lieu` varchar(120) NOT NULL,
  `date_event` date NOT NULL,
  `heure_event` time NOT NULL,
  `prix` double NOT NULL,
  `nb_places` int(11) NOT NULL,
  `categorie` varchar(80) DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'DISPONIBLE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `evenement`
--

INSERT INTO `evenement` (`id_evenement`, `titre`, `description`, `lieu`, `date_event`, `heure_event`, `prix`, `nb_places`, `categorie`, `statut`) VALUES
(1, 'Concert Live Jazz', 'Soirée jazz exceptionnelle avec les meilleurs artistes', 'Carthage Theatre', '2026-03-20', '20:00:00', 50, 200, 'Musique', 'DISPONIBLE'),
(2, 'Festival du Film', 'Projection de films internationaux', 'Cinema Rio', '2026-04-15', '18:00:00', 30, 150, 'Culture', 'DISPONIBLE'),
(3, 'Conférence Tech', 'Innovation et technologies du futur', 'Centre de conférences', '2026-05-10', '09:00:00', 0, 300, 'Technologie', 'DISPONIBLE'),
(4, 'Marathon de Tunis', 'Course de 42km à travers la ville', 'Avenue Habib Bourguiba', '2026-06-01', '07:00:00', 25, 500, 'Sport', 'DISPONIBLE'),
(5, 'Exposition d\'Art', 'Œuvres contemporaines d\'artistes locaux', 'Galerie El Teatro', '2026-03-25', '10:00:00', 15, 100, 'Art', 'DISPONIBLE');

-- --------------------------------------------------------

--
-- Structure de la table `paiement`
--

CREATE TABLE `paiement` (
  `id` int(11) NOT NULL,
  `id_reservation` int(11) NOT NULL,
  `id_utilisateur` int(11) NOT NULL,
  `montant` decimal(10,2) NOT NULL,
  `date_paiement` datetime DEFAULT current_timestamp(),
  `methode_paiement` varchar(50) NOT NULL DEFAULT 'CARTE',
  `statut` varchar(20) NOT NULL DEFAULT 'EN_ATTENTE',
  `reference` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `paiement`
--

INSERT INTO `paiement` (`id`, `id_reservation`, `id_utilisateur`, `montant`, `date_paiement`, `methode_paiement`, `statut`, `reference`, `created_at`) VALUES
(1, 32, 1, 890.00, '2026-02-17 10:21:18', 'ESPECES', 'EN_ATTENTE', 'PAY-1771320078298', '2026-02-17 10:21:18'),
(2, 33, 1, 1350.00, '2026-02-19 21:11:35', 'VIREMENT', 'EN_ATTENTE', 'PAY-1771531895404', '2026-02-19 21:11:35'),
(3, 34, 1, 500.00, '2026-02-27 03:00:09', 'CARTE', 'EN_ATTENTE', 'PAY-1772157609330', '2026-02-27 03:00:09'),
(4, 35, 1, 1500.00, '2026-02-27 03:03:29', 'CARTE', 'EN_ATTENTE', 'PAY-1772157809365', '2026-02-27 03:03:29'),
(5, 36, 1, 450.00, '2026-02-27 03:04:26', 'CARTE', 'EN_ATTENTE', 'PAY-1772157866509', '2026-02-27 03:04:26'),
(6, 37, 6, 890.00, '2026-02-27 21:42:02', 'CARTE', 'EN_ATTENTE', 'PAY-1772224922302', '2026-02-27 21:42:02'),
(7, 38, 6, 450.00, '2026-02-27 22:38:58', 'CARTE', 'EN_ATTENTE', 'PAY-1772228338013', '2026-02-27 22:38:58'),
(8, 39, 6, 450.00, '2026-02-27 22:40:06', 'CARTE', 'EN_ATTENTE', 'PAY-1772228406389', '2026-02-27 22:40:06'),
(9, 40, 6, 299.00, '2026-02-27 22:46:56', 'CARTE', 'EN_ATTENTE', 'PAY-1772228816792', '2026-02-27 22:46:56'),
(10, 41, 6, 900.00, '2026-02-27 23:11:49', 'CARTE', 'EN_ATTENTE', 'PAY-1772230309934', '2026-02-27 23:11:49'),
(11, 42, 6, 650.00, '2026-02-27 23:19:25', 'CARTE', 'EN_ATTENTE', 'PAY-1772230765519', '2026-02-27 23:19:25'),
(12, 43, 10, 650.00, '2026-02-27 23:28:35', 'CARTE', 'EN_ATTENTE', 'PAY-1772231315265', '2026-02-27 23:28:35'),
(13, 44, 10, 800.00, '2026-02-27 23:32:35', 'CARTE', 'EN_ATTENTE', 'PAY-1772231555438', '2026-02-27 23:32:35'),
(14, 45, 10, 890.00, '2026-02-27 23:44:52', 'CARTE', 'EN_ATTENTE', 'PAY-1772232292134', '2026-02-27 23:44:52'),
(16, 47, 10, 450.00, '2026-02-28 00:17:08', 'CARTE', 'EN_ATTENTE', 'PAY-1772234228766', '2026-02-28 00:17:08'),
(17, 48, 10, 299.00, '2026-02-28 00:17:28', 'CARTE', 'EN_ATTENTE', 'PAY-1772234248303', '2026-02-28 00:17:28'),
(18, 49, 6, 100.00, '2026-02-28 01:20:01', 'CARTE', 'EN_ATTENTE', 'PAY-1772238001125', '2026-02-28 01:20:01');

-- --------------------------------------------------------

--
-- Structure de la table `point_interet`
--

CREATE TABLE `point_interet` (
  `id_point_interet` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `type` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `destination_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `point_interet`
--

INSERT INTO `point_interet` (`id_point_interet`, `nom`, `type`, `description`, `image_url`, `destination_id`, `created_at`, `updated_at`) VALUES
(1, 'Plage de Sidi Mahrez', 'plage', 'Magnifique plage de sable blanc', 'plage_mahrez.jpg', 1, '2026-02-16 13:16:01', '2026-02-16 13:16:01'),
(2, 'Synagogue de la Ghriba', 'monument', 'Synagogue historique et lieu de pèlerinage', 'ghriba.jpg', 1, '2026-02-16 13:16:01', '2026-02-16 13:16:01'),
(3, 'Musée de Guellala', 'musée', 'Musée dédié aux traditions de Djerba', 'guellala.jpg', 1, '2026-02-16 13:16:01', '2026-02-16 13:16:01'),
(4, 'Café des Délices', 'restaurant', 'Café emblématique avec vue sur la mer', 'cafe_delices.jpg', 2, '2026-02-16 13:16:01', '2026-02-16 13:16:01');

-- --------------------------------------------------------

--
-- Structure de la table `reclamation`
--

CREATE TABLE `reclamation` (
  `id_reclamation` int(11) NOT NULL,
  `titre` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `type` varchar(80) DEFAULT NULL,
  `statut` varchar(50) DEFAULT 'EN_ATTENTE',
  `date_creation` datetime DEFAULT current_timestamp(),
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reclamation`
--

INSERT INTO `reclamation` (`id_reclamation`, `titre`, `description`, `type`, `statut`, `date_creation`, `id_user`) VALUES
(1, 'Problème de paiement', 'Le paiement n\'a pas été traité correctement', 'Technique', 'EN_ATTENTE', '2026-02-16 14:16:01', 2),
(2, 'Annulation de réservation', 'Je souhaite annuler ma réservation pour le concert', 'Service Client', 'EN_COURS', '2026-02-16 14:16:01', 3),
(3, 'Information manquante', 'Les détails de l\'événement ne sont pas clairs', 'Information', 'TRAITEE', '2026-02-16 14:16:01', 4);

-- --------------------------------------------------------

--
-- Structure de la table `reservationvoyage`
--

CREATE TABLE `reservationvoyage` (
  `id` int(100) NOT NULL,
  `idUtilisateur` int(100) NOT NULL,
  `id_voyage` int(11) NOT NULL,
  `date_reservation` datetime DEFAULT current_timestamp(),
  `statut` enum('EN_ATTENTE','CONFIRMEE','ANNULEE','TERMINEE') NOT NULL DEFAULT 'EN_ATTENTE',
  `nbrPersonnes` int(100) NOT NULL DEFAULT 1,
  `montantTotal` decimal(10,2) NOT NULL DEFAULT 10.20,
  `dateCreation` datetime NOT NULL DEFAULT current_timestamp()
) ;

--
-- Déchargement des données de la table `reservationvoyage`
--

INSERT INTO `reservationvoyage` (`id`, `idUtilisateur`, `id_voyage`, `date_reservation`, `statut`, `nbrPersonnes`, `montantTotal`, `dateCreation`) VALUES
(5, 1, 1, '2026-02-13 15:22:42', 'CONFIRMEE', 2, 900.00, '2026-02-13 15:22:42'),
(6, 2, 2, '2026-02-13 15:22:42', 'EN_ATTENTE', 1, 890.00, '2026-02-13 15:22:42'),
(7, 1, 3, '2026-02-13 00:00:00', 'CONFIRMEE', 4, 2600.00, '2026-02-13 15:22:42'),
(8, 3, 4, '2026-02-13 15:22:42', 'ANNULEE', 2, 598.00, '2026-02-13 15:22:42'),
(9, 2, 5, '2026-02-13 15:22:42', 'EN_ATTENTE', 3, 4500.00, '2026-02-13 15:22:42'),
(10, 5, 4, '2026-02-13 15:22:54', 'CONFIRMEE', 3, 897.00, '2026-02-13 15:22:54'),
(11, 5, 4, '2026-02-13 15:59:55', 'CONFIRMEE', 3, 897.00, '2026-02-13 15:59:55'),
(12, 5, 4, '2026-02-13 16:43:02', 'CONFIRMEE', 3, 897.00, '2026-02-13 16:43:02'),
(19, 67, 1, '2026-02-15 19:20:33', 'EN_ATTENTE', 1, 450.00, '2026-02-15 19:20:33'),
(20, 6666, 1, '2026-03-07 00:00:00', 'EN_ATTENTE', 5, 2250.00, '2026-02-15 23:48:00'),
(21, 6666, 1, '2026-03-07 00:00:00', 'CONFIRMEE', 5, 2250.00, '2026-02-15 23:48:02'),
(22, 1, 1, '2026-02-16 14:16:01', 'CONFIRMEE', 2, 900.00, '2026-02-16 14:16:01'),
(23, 2, 2, '2026-02-16 14:16:01', 'EN_ATTENTE', 1, 890.00, '2026-02-16 14:16:01'),
(24, 1, 3, '2026-02-16 14:16:01', 'CONFIRMEE', 4, 2600.00, '2026-02-16 14:16:01'),
(25, 3, 4, '2026-02-16 14:16:01', 'ANNULEE', 2, 598.00, '2026-02-16 14:16:01'),
(28, 8998, 3, '2026-02-16 00:00:00', 'CONFIRMEE', 3, 1950.00, '2026-02-16 17:26:42'),
(29, 535, 2, '2026-02-12 00:00:00', 'CONFIRMEE', 1, 890.00, '2026-02-17 00:37:53'),
(32, 1, 7, '2026-09-05 00:00:00', 'ANNULEE', 1, 890.00, '2026-02-17 10:21:18'),
(33, 1, 1, '2026-07-11 00:00:00', 'EN_ATTENTE', 3, 1350.00, '2026-02-19 21:11:35'),
(34, 1, 5, '2026-10-20 00:00:00', 'EN_ATTENTE', 1, 500.00, '2026-02-27 03:00:09'),
(35, 1, 10, '2026-10-01 00:00:00', 'EN_ATTENTE', 1, 1500.00, '2026-02-27 03:03:29'),
(36, 1, 1, '2026-07-10 00:00:00', 'ANNULEE', 1, 450.00, '2026-02-27 03:04:26'),
(37, 6, 7, '2026-09-11 00:00:00', 'CONFIRMEE', 1, 890.00, '2026-02-27 21:42:02'),
(38, 6, 1, '2026-10-20 00:00:00', 'ANNULEE', 1, 450.00, '2026-02-27 22:38:58'),
(39, 6, 1, '2026-07-10 00:00:00', 'CONFIRMEE', 1, 450.00, '2026-02-27 22:40:06'),
(40, 6, 4, '2026-06-15 00:00:00', 'CONFIRMEE', 1, 299.00, '2026-02-27 22:46:56'),
(41, 6, 1, '2026-07-10 00:00:00', 'CONFIRMEE', 2, 900.00, '2026-02-27 23:11:49'),
(42, 6, 3, '2026-08-20 00:00:00', 'CONFIRMEE', 1, 650.00, '2026-02-27 23:19:25'),
(43, 10, 3, '2026-08-20 00:00:00', 'CONFIRMEE', 1, 650.00, '2026-02-27 23:28:35'),
(44, 10, 11, '2026-02-16 00:00:00', 'CONFIRMEE', 1, 800.00, '2026-02-27 23:32:35'),
(45, 10, 2, '2026-09-05 00:00:00', 'CONFIRMEE', 1, 890.00, '2026-02-27 23:44:52'),
(47, 10, 6, '2026-07-10 00:00:00', 'CONFIRMEE', 1, 450.00, '2026-02-28 00:17:08'),
(48, 10, 4, '2026-06-15 00:00:00', 'CONFIRMEE', 1, 299.00, '2026-02-28 00:17:28'),
(49, 6, 13, '2026-02-28 00:00:00', 'CONFIRMEE', 1, 100.00, '2026-02-28 01:20:01');

-- --------------------------------------------------------

--
-- Structure de la table `reservation_evenement`
--

CREATE TABLE `reservation_evenement` (
  `id_reservation` int(11) NOT NULL,
  `date_reservation` date NOT NULL,
  `nb_places_reservees` int(11) NOT NULL,
  `statut` varchar(50) DEFAULT 'EN_ATTENTE',
  `id_evenement` int(11) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reservation_evenement`
--

INSERT INTO `reservation_evenement` (`id_reservation`, `date_reservation`, `nb_places_reservees`, `statut`, `id_evenement`, `id_user`) VALUES
(1, '2026-02-15', 2, 'CONFIRMEE', 1, 2),
(2, '2026-02-14', 4, 'EN_ATTENTE', 2, 3),
(3, '2026-02-13', 3, 'CONFIRMEE', 3, 4);

-- --------------------------------------------------------

--
-- Structure de la table `role`
--

CREATE TABLE `role` (
  `id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `role`
--

INSERT INTO `role` (`id`, `name`, `description`) VALUES
(1, 'USER', 'Utilisateur normal - touriste/voyageur'),
(2, 'AGENT', 'Agent de voyage - peut gérer des offres'),
(3, 'ADMIN', 'Administrateur système - tous les droits');

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `avatar_url` varchar(500) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `is_verified` tinyint(1) DEFAULT 0,
  `is_active` tinyint(1) DEFAULT 1,
  `role_id` int(11) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`id`, `email`, `password`, `first_name`, `last_name`, `phone`, `avatar_url`, `description`, `address`, `city`, `country`, `latitude`, `longitude`, `is_verified`, `is_active`, `role_id`, `created_at`, `updated_at`) VALUES
(2, 'agent@test.com', 'password123', 'Janey', 'Smith', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 2, '2026-02-16 18:48:47', '2026-02-16 18:48:47'),
(3, 'admin@test.com', 'admin123', 'Admin', 'System', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 3, '2026-02-16 18:48:47', '2026-02-16 18:48:47'),
(4, 'touriste@email.com', 'pass123', 'Mohamed', 'Ben Ali', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 1, '2026-02-16 18:48:47', '2026-02-16 18:48:47'),
(5, 'agent@voyage.com', 'agent123', 'Sophie', 'Martin', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, 2, '2026-02-16 18:48:47', '2026-02-16 18:48:47'),
(6, 'sabaa.bendziri@esprit.tn', 'soubasouba', 'sabaa', 'dziri', '50960481', NULL, NULL, NULL, 'hhhh', 'Tunisie', NULL, NULL, 1, 1, 1, '2026-02-27 20:41:08', '2026-02-28 01:16:30'),
(8, 'sabsoubbdziri@gmail.com', 'soubasouba', 'sabaa', 'dziri', '50960481', NULL, NULL, NULL, 'tunis', 'Tunisie', NULL, NULL, 0, 1, 2, '2026-02-27 21:16:41', '2026-02-27 21:16:41'),
(10, 'ghorbali02@gmail.com', 'imenimen', 'imen', 'imen', '50960481', NULL, NULL, NULL, 'ttttttttt', 'Tunisie', NULL, NULL, 1, 1, 1, '2026-02-27 22:27:30', '2026-02-27 22:27:30'),
(11, 'medh10054@gmail.com', 'hammahamma', 'hamma', 'hamma', '50960481', NULL, NULL, NULL, 'tunis', 'Tunisie', NULL, NULL, 1, 1, 1, '2026-02-28 18:52:15', '2026-02-28 18:52:15');

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

CREATE TABLE `utilisateur` (
  `id_user` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `mot_de_passe` varchar(255) NOT NULL,
  `role` varchar(50) DEFAULT 'USER'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`id_user`, `nom`, `email`, `mot_de_passe`, `role`) VALUES
(1, 'Administrateur', 'admin@tahwissa.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'ADMIN'),
(2, 'Ahmed Ben Ali', 'ahmed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
(3, 'Sarah Meziani', 'sarah@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
(4, 'Mohamed Karoui', 'mohamed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER'),
(5, 'agent', 'agent@tahwissa.com', 'password', 'USER');

-- --------------------------------------------------------

--
-- Structure de la table `voyage`
--

CREATE TABLE `voyage` (
  `id` int(100) NOT NULL,
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
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `voyage`
--

INSERT INTO `voyage` (`id`, `titre`, `description`, `destination`, `categorie`, `prix_unitaire`, `date_depart`, `date_retour`, `places_disponibles`, `image_url`, `statut`, `created_at`, `updated_at`) VALUES
(1, 'Séjour à Djerba', 'Profitez des plages paradisiaques et du soleil.', 'Djerba', 'Détente', 450.00, '2026-07-10', '2026-07-17', 15, '/images/djerba.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(2, 'Circuit Andalucia', 'Découvrez les villes historiques du sud de l\'Espagne.', 'Espagne', 'Culturel', 890.00, '2026-09-05', '2026-09-12', 8, '/images/andalucia.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(3, 'Aventure en Sardaigne', 'Randonnées et criques secrètes.', 'Sardaigne', 'Aventure', 650.00, '2026-08-20', '2026-08-27', 10, '/images/sardaigne.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(4, 'Week-end à Paris', 'Séjour romantique avec croisière sur la Seine.', 'Paris', 'Romantique', 299.00, '2026-06-15', '2026-06-18', 20, '/images/paris.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(5, 'Safari au Kenya', 'Rencontrez la faune sauvage dans son habitat naturel.', 'Kenya', 'Aventure', 500.00, '2026-10-20', '2026-10-10', 5, '/images/kenya.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-16 17:27:49'),
(6, 'Séjour à Djerba', 'Profitez des plages paradisiaques et du soleil.', 'Djerba', 'Détente', 450.00, '2026-07-10', '2026-07-17', 15, '/images/djerba.jpg', 'ACTIF', '2026-02-16 14:16:01', '2026-02-16 14:16:01'),
(7, 'Circuit Andalucia', 'Découvrez les villes historiques du sud de l\'Espagne.', 'Espagne', 'Culturel', 890.00, '2026-09-05', '2026-09-12', 8, '/images/andalucia.jpg', 'ACTIF', '2026-02-16 14:16:01', '2026-02-16 14:16:01'),
(8, 'Aventure en Sardaigne', 'Randonnées et criques secrètes.', 'Sardaigne', 'Aventure', 650.00, '2026-08-20', '2026-08-27', 10, '/images/sardaigne.jpg', 'ACTIF', '2026-02-16 14:16:01', '2026-02-16 14:16:01'),
(9, 'Week-end à Paris', 'Séjour romantique avec croisière sur la Seine.', 'Paris', 'Romantique', 299.00, '2026-06-15', '2026-06-18', 20, '/images/paris.jpg', 'ACTIF', '2026-02-16 14:16:01', '2026-02-16 14:16:01'),
(10, 'Safari au Kenya', 'Rencontrez la faune sauvage dans son habitat naturel.', 'Kenya', 'Aventure', 1500.00, '2026-10-01', '2026-10-10', 5, '/images/kenya.jpg', 'ACTIF', '2026-02-16 14:16:01', '2026-02-16 14:16:01'),
(11, 'Egypt', 'bbbbbbbb', 'aaaaaaaaa', 'ssssssss', 800.00, '2026-02-16', '2026-02-23', 10, 'https://share.google/atPEuVVyYbUu82C0f', 'ACTIF', '2026-02-16 17:24:48', '2026-02-16 17:24:48'),
(13, 'tabarka', '', 'tabarka', 'camping', 100.00, '2026-02-28', '2026-03-05', 7, '', 'ACTIF', '2026-02-28 01:19:14', '2026-02-28 01:19:14'),
(14, 'tabarka', '', 'tabarka', 'camping', 100.00, '2026-02-28', '2026-03-05', 7, '', 'ACTIF', '2026-02-28 01:19:16', '2026-02-28 01:19:16');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `destination`
--
ALTER TABLE `destination`
  ADD PRIMARY KEY (`id_destination`);

--
-- Index pour la table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`id_evenement`);

--
-- Index pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_reservation` (`id_reservation`);

--
-- Index pour la table `point_interet`
--
ALTER TABLE `point_interet`
  ADD PRIMARY KEY (`id_point_interet`),
  ADD KEY `destination_id` (`destination_id`);

--
-- Index pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD PRIMARY KEY (`id_reclamation`),
  ADD KEY `id_user` (`id_user`);

--
-- Index pour la table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idUtilisateur` (`idUtilisateur`),
  ADD KEY `idDestination` (`id_voyage`);

--
-- Index pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD PRIMARY KEY (`id_reservation`),
  ADD KEY `id_evenement` (`id_evenement`),
  ADD KEY `id_user` (`id_user`);

--
-- Index pour la table `role`
--
ALTER TABLE `role`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `role_id` (`role_id`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Index pour la table `voyage`
--
ALTER TABLE `voyage`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `destination`
--
ALTER TABLE `destination`
  MODIFY `id_destination` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `id_evenement` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `paiement`
--
ALTER TABLE `paiement`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT pour la table `point_interet`
--
ALTER TABLE `point_interet`
  MODIFY `id_point_interet` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `reclamation`
--
ALTER TABLE `reclamation`
  MODIFY `id_reclamation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  MODIFY `id_reservation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `role`
--
ALTER TABLE `role`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `voyage`
--
ALTER TABLE `voyage`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `paiement`
--
ALTER TABLE `paiement`
  ADD CONSTRAINT `paiement_ibfk_1` FOREIGN KEY (`id_reservation`) REFERENCES `reservationvoyage` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `point_interet`
--
ALTER TABLE `point_interet`
  ADD CONSTRAINT `point_interet_ibfk_1` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`id_destination`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD CONSTRAINT `fk_reclamation_user` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  ADD CONSTRAINT `fk_reservation_voyage` FOREIGN KEY (`id_voyage`) REFERENCES `voyage` (`id`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD CONSTRAINT `fk_reservation_evenement_evenement` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`id_evenement`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reservation_evenement_user` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE;

--
-- Contraintes pour la table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

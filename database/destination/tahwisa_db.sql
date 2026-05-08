-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 16 fév. 2026 à 13:21
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `tahwisa_db`
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
(1, 'Djerba', 'Tunisie', 'djerbahood', 'Île paradisiaque connue pour ses plages et son patrimoine culturel', 'djerba.jpg', 33.80760000, 10.84510000, '2026-02-10 02:13:23', '2026-02-16 12:01:30'),
(2, 'Sidi Bou Said', 'Tunisie', 'Tunis', 'Village pittoresque aux maisons bleues et blanches', 'sidibousaid.jpg', 36.86860000, 10.34110000, '2026-02-10 02:13:23', '2026-02-10 02:13:23');

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
(1, 'Plage de Sidi Mahrez', 'plage', 'Magnifique plage de sable blanc', 'plage_mahrez.jpg', 1, '2026-02-10 02:13:24', '2026-02-10 02:13:24'),
(2, 'Synagogue de la Ghriba', 'monument', 'Synagogue historique et lieu de pèlerinage', 'ghriba.jpg', 1, '2026-02-10 02:13:24', '2026-02-10 02:13:24'),
(3, 'Musée de Guellala', 'musée', 'Musée dédié aux traditions de Djerba', 'guellala.jpg', 1, '2026-02-10 02:13:24', '2026-02-10 02:13:24'),
(4, 'Café des Délices', 'restaurant', 'Café emblématique avec vue sur la mer', 'cafe_delices.jpg', 2, '2026-02-10 02:13:24', '2026-02-10 02:13:24');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `destination`
--
ALTER TABLE `destination`
  ADD PRIMARY KEY (`id_destination`);

--
-- Index pour la table `point_interet`
--
ALTER TABLE `point_interet`
  ADD PRIMARY KEY (`id_point_interet`),
  ADD KEY `destination_id` (`destination_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `destination`
--
ALTER TABLE `destination`
  MODIFY `id_destination` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `point_interet`
--
ALTER TABLE `point_interet`
  MODIFY `id_point_interet` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `point_interet`
--
ALTER TABLE `point_interet`
  ADD CONSTRAINT `point_interet_ibfk_1` FOREIGN KEY (`destination_id`) REFERENCES `destination` (`id_destination`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

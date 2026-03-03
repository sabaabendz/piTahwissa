-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : lun. 16 fév. 2026 à 14:24
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `evenementbd`
--

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
  `statut` varchar(50) DEFAULT 'DISPONIBLE',
  `date_creation` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `evenement`
--

INSERT INTO `evenement` (`id_evenement`, `titre`, `description`, `lieu`, `date_event`, `heure_event`, `prix`, `nb_places`, `categorie`, `statut`, `date_creation`) VALUES
(1, 'Concert Live Jazz', 'Soirée jazz exceptionnelle avec les meilleurs artistes', 'Carthage Theatre', '2026-03-20', '20:00:00', 50, 200, 'Musique', 'DISPONIBLE', '2026-02-16 13:56:37'),
(2, 'Festival du Film', 'Projection de films internationaux', 'Cinema Rio', '2026-04-15', '18:00:00', 30, 150, 'Culture', 'DISPONIBLE', '2026-02-16 13:56:37'),
(3, 'Conférence Tech', 'Innovation et technologies du futur', 'Centre de conférences', '2026-05-10', '09:00:00', 0, 300, 'Technologie', 'DISPONIBLE', '2026-02-16 13:56:37'),
(5, 'Exposition d\'Art', 'Œuvres contemporaines d\'artistes locaux', 'Galerie El Teatro', '2026-03-25', '10:00:00', 15, 100, 'Art', 'DISPONIBLE', '2026-02-16 13:56:37');

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
(1, 'Problème de paiement', 'Le paiement n\'a pas été traité correctement', 'Technique', 'EN_ATTENTE', '2026-02-16 12:36:04', 2),
(2, 'Annulation de réservation', 'Je souhaite annuler ma réservation pour le concert', 'Service Client', 'EN_COURS', '2026-02-16 12:36:04', 3);

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
(2, '2026-02-14', 4, 'EN_ATTENTE', 2, 3),
(3, '2026-02-13', 3, 'CONFIRMEE', 3, 4);

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
(4, 'Mohamed Karoui', 'mohamed@example.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'USER');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `evenement`
--
ALTER TABLE `evenement`
  ADD PRIMARY KEY (`id_evenement`);

--
-- Index pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD PRIMARY KEY (`id_reclamation`),
  ADD KEY `id_user` (`id_user`);

--
-- Index pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD PRIMARY KEY (`id_reservation`),
  ADD KEY `id_evenement` (`id_evenement`),
  ADD KEY `id_user` (`id_user`);

--
-- Index pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `evenement`
--
ALTER TABLE `evenement`
  MODIFY `id_evenement` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `reclamation`
--
ALTER TABLE `reclamation`
  MODIFY `id_reclamation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  MODIFY `id_reservation` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `utilisateur`
--
ALTER TABLE `utilisateur`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `reclamation`
--
ALTER TABLE `reclamation`
  ADD CONSTRAINT `reclamation_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reservation_evenement`
--
ALTER TABLE `reservation_evenement`
  ADD CONSTRAINT `reservation_evenement_ibfk_1` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`id_evenement`) ON DELETE CASCADE,
  ADD CONSTRAINT `reservation_evenement_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

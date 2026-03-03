-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 15, 2026 at 04:34 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestionreservation`
--

-- --------------------------------------------------------

--
-- Table structure for table `reservationvoyage`
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
-- Dumping data for table `reservationvoyage`
--

INSERT INTO `reservationvoyage` (`id`, `idUtilisateur`, `id_voyage`, `date_reservation`, `statut`, `nbrPersonnes`, `montantTotal`, `dateCreation`) VALUES
(5, 1, 1, '2026-02-13 15:22:42', 'CONFIRMEE', 2, 900.00, '2026-02-13 15:22:42'),
(6, 2, 2, '2026-02-13 15:22:42', 'EN_ATTENTE', 1, 890.00, '2026-02-13 15:22:42'),
(7, 1, 3, '2026-02-13 15:22:42', 'CONFIRMEE', 4, 2600.00, '2026-02-13 15:22:42'),
(8, 3, 4, '2026-02-13 15:22:42', 'ANNULEE', 2, 598.00, '2026-02-13 15:22:42'),
(9, 2, 5, '2026-02-13 15:22:42', 'EN_ATTENTE', 3, 4500.00, '2026-02-13 15:22:42'),
(10, 5, 4, '2026-02-13 15:22:54', 'CONFIRMEE', 3, 897.00, '2026-02-13 15:22:54'),
(11, 5, 4, '2026-02-13 15:59:55', 'CONFIRMEE', 3, 897.00, '2026-02-13 15:59:55'),
(12, 5, 4, '2026-02-13 16:43:02', 'CONFIRMEE', 3, 897.00, '2026-02-13 16:43:02'),
(13, 5, 4, '2026-02-13 16:57:57', 'CONFIRMEE', 3, 897.00, '2026-02-13 16:57:56');

-- --------------------------------------------------------

--
-- Table structure for table `voyage`
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
-- Dumping data for table `voyage`
--

INSERT INTO `voyage` (`id`, `titre`, `description`, `destination`, `categorie`, `prix_unitaire`, `date_depart`, `date_retour`, `places_disponibles`, `image_url`, `statut`, `created_at`, `updated_at`) VALUES
(1, 'Séjour à Djerba', 'Profitez des plages paradisiaques et du soleil.', 'Djerba', 'Détente', 450.00, '2026-07-10', '2026-07-17', 15, '/images/djerba.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(2, 'Circuit Andalucia', 'Découvrez les villes historiques du sud de l\'Espagne.', 'Espagne', 'Culturel', 890.00, '2026-09-05', '2026-09-12', 8, '/images/andalucia.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(3, 'Aventure en Sardaigne', 'Randonnées et criques secrètes.', 'Sardaigne', 'Aventure', 650.00, '2026-08-20', '2026-08-27', 10, '/images/sardaigne.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(4, 'Week-end à Paris', 'Séjour romantique avec croisière sur la Seine.', 'Paris', 'Romantique', 299.00, '2026-06-15', '2026-06-18', 20, '/images/paris.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10'),
(5, 'Safari au Kenya', 'Rencontrez la faune sauvage dans son habitat naturel.', 'Kenya', 'Aventure', 1500.00, '2026-10-01', '2026-10-10', 5, '/images/kenya.jpg', 'ACTIF', '2026-02-13 15:22:10', '2026-02-13 15:22:10');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idUtilisateur` (`idUtilisateur`),
  ADD KEY `idDestination` (`id_voyage`);

--
-- Indexes for table `voyage`
--
ALTER TABLE `voyage`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `voyage`
--
ALTER TABLE `voyage`
  MODIFY `id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `reservationvoyage`
--
ALTER TABLE `reservationvoyage`
  ADD CONSTRAINT `fk_reservation_voyage` FOREIGN KEY (`id_voyage`) REFERENCES `voyage` (`id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

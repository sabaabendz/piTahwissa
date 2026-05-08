-- Add like/dislike support for events (evenementbd)
-- Run this script in MySQL: source add_event_reactions.sql; or paste in MySQL client

USE evenementbd;

CREATE TABLE IF NOT EXISTS `evenement_reaction` (
  `id_reaction` int(11) NOT NULL AUTO_INCREMENT,
  `id_evenement` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `type` varchar(10) NOT NULL,
  `date_creation` datetime DEFAULT current_timestamp(),
  PRIMARY KEY (`id_reaction`),
  UNIQUE KEY `uq_evenement_user` (`id_evenement`, `id_user`),
  KEY `fk_reaction_evenement` (`id_evenement`),
  KEY `fk_reaction_user` (`id_user`),
  CONSTRAINT `evenement_reaction_ibfk_1` FOREIGN KEY (`id_evenement`) REFERENCES `evenement` (`id_evenement`) ON DELETE CASCADE,
  CONSTRAINT `evenement_reaction_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE,
  CONSTRAINT `chk_type` CHECK (`type` IN ('LIKE', 'DISLIKE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260405162035 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL, available_at DATETIME NOT NULL, delivered_at DATETIME DEFAULT NULL, INDEX IDX_75EA56E0FB7336F0E3BD61CE16BA31DBBF396750 (queue_name, available_at, delivered_at, id), PRIMARY KEY (id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci`');
        $this->addSql('ALTER TABLE evenement_reaction DROP FOREIGN KEY `fk_reaction_evenement`');
        $this->addSql('ALTER TABLE evenement_reaction DROP FOREIGN KEY `fk_reaction_user`');
        $this->addSql('ALTER TABLE paiement DROP FOREIGN KEY `fk_paiement_reservation`');
        $this->addSql('ALTER TABLE paiement DROP FOREIGN KEY `fk_paiement_utilisateur`');
        $this->addSql('ALTER TABLE point_interet DROP FOREIGN KEY `point_interet_ibfk_1`');
        $this->addSql('ALTER TABLE reclamation DROP FOREIGN KEY `fk_reclamation_user`');
        $this->addSql('ALTER TABLE reservationvoyage DROP FOREIGN KEY `fk_reservation_voyage`');
        $this->addSql('ALTER TABLE reservation_evenement DROP FOREIGN KEY `fk_reservation_evenement_evenement`');
        $this->addSql('ALTER TABLE reservation_evenement DROP FOREIGN KEY `fk_reservation_evenement_user`');
        $this->addSql('ALTER TABLE reservation_transport DROP FOREIGN KEY `fk_reservation_transport_transport`');
        $this->addSql('ALTER TABLE reservation_transport DROP FOREIGN KEY `fk_reservation_transport_user`');
        $this->addSql('DROP TABLE collaborator');
        $this->addSql('DROP TABLE destination');
        $this->addSql('DROP TABLE evenement');
        $this->addSql('DROP TABLE evenement_reaction');
        $this->addSql('DROP TABLE manager');
        $this->addSql('DROP TABLE paiement');
        $this->addSql('DROP TABLE point_interet');
        $this->addSql('DROP TABLE reclamation');
        $this->addSql('DROP TABLE reservationvoyage');
        $this->addSql('DROP TABLE reservation_evenement');
        $this->addSql('DROP TABLE reservation_transport');
        $this->addSql('DROP TABLE transport');
        $this->addSql('DROP TABLE utilisateur');
        $this->addSql('DROP TABLE voyage');
        $this->addSql('DROP INDEX name ON role');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_57698A6A5E237E06 ON role (name)');
        $this->addSql('ALTER TABLE user DROP FOREIGN KEY `user_ibfk_1`');
        $this->addSql('ALTER TABLE user CHANGE description description LONGTEXT DEFAULT NULL, CHANGE is_verified is_verified TINYINT DEFAULT 0 NOT NULL, CHANGE is_active is_active TINYINT DEFAULT 1 NOT NULL, CHANGE role_id role_id INT DEFAULT NULL, CHANGE created_at created_at DATETIME NOT NULL, CHANGE updated_at updated_at DATETIME NOT NULL');
        $this->addSql('DROP INDEX email ON user');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_8D93D649E7927C74 ON user (email)');
        $this->addSql('DROP INDEX role_id ON user');
        $this->addSql('CREATE INDEX IDX_8D93D649D60322AC ON user (role_id)');
        $this->addSql('ALTER TABLE user ADD CONSTRAINT `user_ibfk_1` FOREIGN KEY (role_id) REFERENCES role (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE collaborator (post VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, team VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, iduser INT NOT NULL, PRIMARY KEY (iduser)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE destination (id_destination INT AUTO_INCREMENT NOT NULL, nom VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, pays VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, ville VARCHAR(100) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_unicode_ci`, description TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_unicode_ci`, image_url VARCHAR(255) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_unicode_ci`, latitude NUMERIC(10, 8) DEFAULT NULL, longitude NUMERIC(11, 8) DEFAULT NULL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, PRIMARY KEY (id_destination)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE evenement (id_evenement INT AUTO_INCREMENT NOT NULL, titre VARCHAR(150) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, description TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, lieu VARCHAR(120) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, date_event DATE NOT NULL, heure_event TIME NOT NULL, prix DOUBLE PRECISION NOT NULL, nb_places INT NOT NULL, categorie VARCHAR(80) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, statut VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'DISPONIBLE\' COLLATE `utf8mb4_general_ci`, image_filename VARCHAR(255) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, date_creation DATETIME DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id_evenement)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE evenement_reaction (id_reaction INT AUTO_INCREMENT NOT NULL, id_evenement INT NOT NULL, id_user INT NOT NULL, type VARCHAR(10) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, date_creation DATETIME DEFAULT CURRENT_TIMESTAMP, INDEX fk_reaction_user (id_user), UNIQUE INDEX uq_evenement_user (id_evenement, id_user), INDEX fk_reaction_evenement (id_evenement), PRIMARY KEY (id_reaction)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE manager (levem VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, department VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, iduser INT NOT NULL, PRIMARY KEY (iduser)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE paiement (id INT AUTO_INCREMENT NOT NULL, id_reservation INT NOT NULL, id_utilisateur INT NOT NULL, montant NUMERIC(10, 2) NOT NULL, date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP, methode_paiement VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'CARTE\' NOT NULL COLLATE `utf8mb4_general_ci`, statut VARCHAR(20) CHARACTER SET utf8mb4 DEFAULT \'EN_ATTENTE\' NOT NULL COLLATE `utf8mb4_general_ci`, reference VARCHAR(100) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, INDEX id_reservation (id_reservation), INDEX id_utilisateur (id_utilisateur), PRIMARY KEY (id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE point_interet (id_point_interet INT AUTO_INCREMENT NOT NULL, nom VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, type VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, description TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_unicode_ci`, image_url VARCHAR(255) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_unicode_ci`, destination_id INT NOT NULL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, INDEX destination_id (destination_id), PRIMARY KEY (id_point_interet)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE reclamation (id_reclamation INT AUTO_INCREMENT NOT NULL, titre VARCHAR(150) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, description TEXT CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, type VARCHAR(80) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, statut VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'EN_ATTENTE\' COLLATE `utf8mb4_general_ci`, date_creation DATETIME DEFAULT CURRENT_TIMESTAMP, id_user INT DEFAULT NULL, INDEX id_user (id_user), PRIMARY KEY (id_reclamation)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE reservationvoyage (id INT AUTO_INCREMENT NOT NULL, idUtilisateur INT NOT NULL, id_voyage INT NOT NULL, date_reservation DATETIME DEFAULT CURRENT_TIMESTAMP, statut ENUM(\'EN_ATTENTE\', \'CONFIRMEE\', \'ANNULEE\', \'TERMINEE\') CHARACTER SET utf8mb4 DEFAULT \'EN_ATTENTE\' NOT NULL COLLATE `utf8mb4_general_ci`, nbrPersonnes INT DEFAULT 1 NOT NULL, montantTotal NUMERIC(10, 2) DEFAULT \'10.20\' NOT NULL, dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, INDEX idUtilisateur (idUtilisateur), INDEX id_voyage (id_voyage), PRIMARY KEY (id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE reservation_evenement (id_reservation INT AUTO_INCREMENT NOT NULL, date_reservation DATE NOT NULL, nb_places_reservees INT NOT NULL, statut VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'EN_ATTENTE\' COLLATE `utf8mb4_general_ci`, id_evenement INT DEFAULT NULL, id_user INT DEFAULT NULL, INDEX id_evenement (id_evenement), INDEX id_user (id_user), PRIMARY KEY (id_reservation)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE reservation_transport (id_reservation INT AUTO_INCREMENT NOT NULL, date_reservation DATE NOT NULL, nb_places_reservees INT NOT NULL, statut VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'EN_ATTENTE\' NOT NULL COLLATE `utf8mb4_unicode_ci`, id_transport INT NOT NULL, id_user INT NOT NULL, INDEX id_transport (id_transport), INDEX id_user (id_user), PRIMARY KEY (id_reservation)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE transport (id_transport INT AUTO_INCREMENT NOT NULL, type_transport VARCHAR(50) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, ville_depart VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, ville_arrivee VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, date_depart DATE NOT NULL, heure_depart TIME NOT NULL, duree INT NOT NULL, prix DOUBLE PRECISION NOT NULL, nb_places INT NOT NULL, PRIMARY KEY (id_transport)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE utilisateur (id_user INT AUTO_INCREMENT NOT NULL, nom VARCHAR(100) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, email VARCHAR(150) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, mot_de_passe VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, role VARCHAR(50) CHARACTER SET utf8mb4 DEFAULT \'USER\' COLLATE `utf8mb4_general_ci`, UNIQUE INDEX email (email), PRIMARY KEY (id_user)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('CREATE TABLE voyage (id INT AUTO_INCREMENT NOT NULL, titre VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, description TEXT CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, destination VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_general_ci`, categorie VARCHAR(100) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, prix_unitaire NUMERIC(10, 2) NOT NULL, date_depart DATE DEFAULT NULL, date_retour DATE DEFAULT NULL, places_disponibles INT DEFAULT 0 NOT NULL, image_url VARCHAR(255) CHARACTER SET utf8mb4 DEFAULT NULL COLLATE `utf8mb4_general_ci`, statut VARCHAR(20) CHARACTER SET utf8mb4 DEFAULT \'ACTIF\' COLLATE `utf8mb4_general_ci`, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_general_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('ALTER TABLE evenement_reaction ADD CONSTRAINT `fk_reaction_evenement` FOREIGN KEY (id_evenement) REFERENCES evenement (id_evenement) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE evenement_reaction ADD CONSTRAINT `fk_reaction_user` FOREIGN KEY (id_user) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE paiement ADD CONSTRAINT `fk_paiement_reservation` FOREIGN KEY (id_reservation) REFERENCES reservationvoyage (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE paiement ADD CONSTRAINT `fk_paiement_utilisateur` FOREIGN KEY (id_utilisateur) REFERENCES utilisateur (id_user) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE point_interet ADD CONSTRAINT `point_interet_ibfk_1` FOREIGN KEY (destination_id) REFERENCES destination (id_destination) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reclamation ADD CONSTRAINT `fk_reclamation_user` FOREIGN KEY (id_user) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reservationvoyage ADD CONSTRAINT `fk_reservation_voyage` FOREIGN KEY (id_voyage) REFERENCES voyage (id) ON UPDATE CASCADE ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reservation_evenement ADD CONSTRAINT `fk_reservation_evenement_evenement` FOREIGN KEY (id_evenement) REFERENCES evenement (id_evenement) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reservation_evenement ADD CONSTRAINT `fk_reservation_evenement_user` FOREIGN KEY (id_user) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reservation_transport ADD CONSTRAINT `fk_reservation_transport_transport` FOREIGN KEY (id_transport) REFERENCES transport (id_transport) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE reservation_transport ADD CONSTRAINT `fk_reservation_transport_user` FOREIGN KEY (id_user) REFERENCES user (id) ON DELETE CASCADE');
        $this->addSql('DROP TABLE messenger_messages');
        $this->addSql('DROP INDEX uniq_57698a6a5e237e06 ON role');
        $this->addSql('CREATE UNIQUE INDEX name ON role (name)');
        $this->addSql('ALTER TABLE user DROP FOREIGN KEY FK_8D93D649D60322AC');
        $this->addSql('ALTER TABLE user CHANGE description description TEXT DEFAULT NULL, CHANGE is_verified is_verified TINYINT DEFAULT 0, CHANGE is_active is_active TINYINT DEFAULT 1, CHANGE created_at created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, CHANGE updated_at updated_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, CHANGE role_id role_id INT DEFAULT 1');
        $this->addSql('DROP INDEX uniq_8d93d649e7927c74 ON user');
        $this->addSql('CREATE UNIQUE INDEX email ON user (email)');
        $this->addSql('DROP INDEX idx_8d93d649d60322ac ON user');
        $this->addSql('CREATE INDEX role_id ON user (role_id)');
        $this->addSql('ALTER TABLE user ADD CONSTRAINT FK_8D93D649D60322AC FOREIGN KEY (role_id) REFERENCES role (id)');
    }
}

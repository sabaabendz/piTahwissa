<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260201215055 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE collaborator (post VARCHAR(255) NOT NULL, team VARCHAR(255) NOT NULL, iduser INT NOT NULL, PRIMARY KEY (iduser)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci`');
        $this->addSql('CREATE TABLE manager (levem VARCHAR(255) NOT NULL, department VARCHAR(255) NOT NULL, iduser INT NOT NULL, PRIMARY KEY (iduser)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci`');
        $this->addSql('CREATE TABLE user (iduser INT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, type VARCHAR(255) NOT NULL, PRIMARY KEY (iduser)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci`');
        $this->addSql('CREATE TABLE messenger_messages (id BIGINT AUTO_INCREMENT NOT NULL, body LONGTEXT NOT NULL, headers LONGTEXT NOT NULL, queue_name VARCHAR(190) NOT NULL, created_at DATETIME NOT NULL, available_at DATETIME NOT NULL, delivered_at DATETIME DEFAULT NULL, INDEX IDX_75EA56E0FB7336F0E3BD61CE16BA31DBBF396750 (queue_name, available_at, delivered_at, id), PRIMARY KEY (id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci`');
        $this->addSql('ALTER TABLE collaborator ADD CONSTRAINT FK_606D487C5E5C27E9 FOREIGN KEY (iduser) REFERENCES user (iduser) ON DELETE CASCADE');
        $this->addSql('ALTER TABLE manager ADD CONSTRAINT FK_FA2425B95E5C27E9 FOREIGN KEY (iduser) REFERENCES user (iduser) ON DELETE CASCADE');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE collaborator DROP FOREIGN KEY FK_606D487C5E5C27E9');
        $this->addSql('ALTER TABLE manager DROP FOREIGN KEY FK_FA2425B95E5C27E9');
        $this->addSql('DROP TABLE collaborator');
        $this->addSql('DROP TABLE manager');
        $this->addSql('DROP TABLE user');
        $this->addSql('DROP TABLE messenger_messages');
    }
}

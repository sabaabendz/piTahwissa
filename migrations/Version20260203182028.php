<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20260203182028 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE collaborator ADD enterprise_code VARCHAR(20) NOT NULL');
        $this->addSql('ALTER TABLE manager ADD enterprise_code VARCHAR(20) NOT NULL, CHANGE levem level VARCHAR(255) NOT NULL');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_FA2425B93CDF284 ON manager (enterprise_code)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE collaborator DROP enterprise_code');
        $this->addSql('DROP INDEX UNIQ_FA2425B93CDF284 ON manager');
        $this->addSql('ALTER TABLE manager DROP enterprise_code, CHANGE level levem VARCHAR(255) NOT NULL');
    }
}

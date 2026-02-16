-- Script pour vérifier et mettre à jour la structure de la table user

-- Afficher la structure actuelle de la table
DESCRIBE user;

-- Si les colonnes phone, city, country n'existent pas, les ajouter:
-- ALTER TABLE user ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
-- ALTER TABLE user ADD COLUMN IF NOT EXISTS city VARCHAR(100);
-- ALTER TABLE user ADD COLUMN IF NOT EXISTS country VARCHAR(100);
-- ALTER TABLE user ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT FALSE;
-- ALTER TABLE user ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;


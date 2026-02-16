-- Script pour vérifier et corriger la structure de la table user

-- 1. Afficher la structure actuelle de la table
DESCRIBE user;

-- 2. Si des colonnes manquent, les ajouter avec ces commandes:

-- Ajouter la colonne phone si elle n'existe pas
-- ALTER TABLE user ADD COLUMN phone VARCHAR(20) AFTER last_name;

-- Ajouter la colonne city si elle n'existe pas
-- ALTER TABLE user ADD COLUMN city VARCHAR(100) AFTER phone;

-- Ajouter la colonne country si elle n'existe pas
-- ALTER TABLE user ADD COLUMN country VARCHAR(100) AFTER city;

-- Ajouter la colonne is_verified si elle n'existe pas
-- ALTER TABLE user ADD COLUMN is_verified BOOLEAN DEFAULT FALSE AFTER country;

-- Ajouter la colonne is_active si elle n'existe pas
-- ALTER TABLE user ADD COLUMN is_active BOOLEAN DEFAULT TRUE AFTER is_verified;

-- 3. Structure finale attendue:
/*
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    city VARCHAR(100),
    country VARCHAR(100),
    role_id INT NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(id)
);
*/

-- 4. Vérifier les données dans la table role
SELECT * FROM role;

-- Les rôles doivent être:
-- id=1: USER ou VOYAGEUR
-- id=2: AGENT ou GUIDE
-- id=3: ADMIN

-- 5. Si la table role n'existe pas ou est vide, créer les données:
/*
CREATE TABLE IF NOT EXISTS role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO role (id, name) VALUES
    (1, 'USER'),
    (2, 'AGENT'),
    (3, 'ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);
*/


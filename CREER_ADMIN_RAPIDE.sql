-- ===============================================
-- COPIER-COLLER CE SCRIPT DANS phpMyAdmin
-- Et cliquer sur "Exécuter" / "Go"
-- ===============================================

-- Étape 1: Créer les rôles
INSERT INTO role (id, name) VALUES
    (1, 'USER'),
    (2, 'AGENT'),
    (3, 'ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Étape 2: Supprimer l'ancien compte s'il existe
DELETE FROM user WHERE email = 'admin@tahwissa.com';

-- Étape 3: Créer le compte ADMIN
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES ('admin@tahwissa.com', 'admin123', 'Admin', 'Principal', '+216 70 000 001', 'Tunis', 'Tunisie', 3, 1, 1);

-- Étape 4: Vérifier
SELECT
    u.email,
    CONCAT(u.first_name, ' ', u.last_name) as nom_complet,
    r.name as role,
    u.is_verified,
    u.is_active
FROM user u
JOIN role r ON u.role_id = r.id
WHERE u.email = 'admin@tahwissa.com';

-- ===============================================
-- COMPTE CRÉÉ:
-- Email: admin@tahwissa.com
-- Password: admin123
-- Rôle: ADMIN
-- ===============================================


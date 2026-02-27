-- Script pour créer des comptes de test (ADMIN et AGENT)

-- 1. Vérifier que la table role existe et a les bonnes données
SELECT * FROM role;

-- Si la table role est vide ou n'existe pas, créer les rôles:
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

-- 2. Créer un compte ADMIN de test
-- Email: admin@tahwissa.com
-- Password: admin123
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES 
    ('admin@tahwissa.com', 'admin123', 'Admin', 'Principal', '+216 70 000 001', 'Tunis', 'Tunisie', 3, TRUE, TRUE)
ON DUPLICATE KEY UPDATE 
    password = 'admin123',
    first_name = 'Admin',
    last_name = 'Principal',
    role_id = 3;

-- 3. Créer un compte AGENT de test
-- Email: agent@tahwissa.com
-- Password: agent123
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES 
    ('agent@tahwissa.com', 'agent123', 'Agent', 'Support', '+216 70 000 002', 'Tunis', 'Tunisie', 2, TRUE, TRUE)
ON DUPLICATE KEY UPDATE 
    password = 'agent123',
    first_name = 'Agent',
    last_name = 'Support',
    role_id = 2;

-- 4. Créer un compte USER/VOYAGEUR de test (pour vérifier le blocage)
-- Email: user@tahwissa.com
-- Password: user123
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES 
    ('user@tahwissa.com', 'user123', 'Mohamed', 'User', '+216 70 000 003', 'Tunis', 'Tunisie', 1, TRUE, TRUE)
ON DUPLICATE KEY UPDATE 
    password = 'user123',
    first_name = 'Mohamed',
    last_name = 'User',
    role_id = 1;

-- 5. Vérifier que les utilisateurs ont été créés
SELECT 
    u.id,
    u.email,
    u.first_name,
    u.last_name,
    r.name as role,
    u.is_active,
    u.is_verified
FROM user u
JOIN role r ON u.role_id = r.id
WHERE u.email IN ('admin@tahwissa.com', 'agent@tahwissa.com', 'user@tahwissa.com');

-- RÉSUMÉ DES COMPTES DE TEST:
-- ═══════════════════════════════════════════════════════════════
-- 
-- 1. COMPTE ADMIN (Accès dashboard ✅)
--    Email:    admin@tahwissa.com
--    Password: admin123
--    Rôle:     ADMIN
-- 
-- 2. COMPTE AGENT (Accès dashboard ✅)
--    Email:    agent@tahwissa.com
--    Password: agent123
--    Rôle:     AGENT
-- 
-- 3. COMPTE USER (Accès dashboard ❌ - Bloqué)
--    Email:    user@tahwissa.com
--    Password: user123
--    Rôle:     USER/VOYAGEUR
-- 
-- ═══════════════════════════════════════════════════════════════

-- ===================================================================
-- SCRIPT RAPIDE POUR CRÉER UN COMPTE ADMIN
-- ===================================================================
-- Exécutez ce script dans phpMyAdmin pour créer immédiatement
-- un compte admin fonctionnel
-- ===================================================================

-- 1. Vérifier que la table role existe et contient les données
SELECT * FROM role;

-- Si la table role est vide, exécutez d'abord ceci:
/*
INSERT INTO role (id, name) VALUES 
    (1, 'USER'),
    (2, 'AGENT'),
    (3, 'ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);
*/

-- 2. SUPPRIMER l'ancien compte admin s'il existe (pour éviter les doublons)
DELETE FROM user WHERE email = 'admin@tahwissa.com';

-- 3. CRÉER le compte ADMIN
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES ('admin@tahwissa.com', 'admin123', 'Admin', 'Principal', '+216 70 000 001', 'Tunis', 'Tunisie', 3, 1, 1);

-- 4. VÉRIFIER que le compte a été créé
SELECT 
    u.id,
    u.email,
    u.first_name,
    u.last_name,
    u.phone,
    u.city,
    u.country,
    r.name as role,
    u.is_verified,
    u.is_active
FROM user u
JOIN role r ON u.role_id = r.id
WHERE u.email = 'admin@tahwissa.com';

-- ===================================================================
-- RÉSULTAT ATTENDU:
-- ===================================================================
-- Vous devriez voir une ligne avec:
--   email: admin@tahwissa.com
--   first_name: Admin
--   last_name: Principal
--   role: ADMIN
--   is_verified: 1
--   is_active: 1
-- ===================================================================

-- ===================================================================
-- COMPTES SUPPLÉMENTAIRES (optionnel)
-- ===================================================================

-- Compte AGENT
DELETE FROM user WHERE email = 'agent@tahwissa.com';
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES ('agent@tahwissa.com', 'agent123', 'Agent', 'Support', '+216 70 000 002', 'Tunis', 'Tunisie', 2, 1, 1);

-- Compte USER (pour tester le blocage)
DELETE FROM user WHERE email = 'user@tahwissa.com';
INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active)
VALUES ('user@tahwissa.com', 'user123', 'Mohamed', 'User', '+216 70 000 003', 'Tunis', 'Tunisie', 1, 1, 1);

-- ===================================================================
-- VÉRIFICATION FINALE - Tous les comptes
-- ===================================================================
SELECT 
    u.id,
    u.email,
    CONCAT(u.first_name, ' ', u.last_name) as nom_complet,
    r.name as role,
    u.is_verified,
    u.is_active
FROM user u
JOIN role r ON u.role_id = r.id
WHERE u.email IN ('admin@tahwissa.com', 'agent@tahwissa.com', 'user@tahwissa.com')
ORDER BY u.role_id DESC;

-- ===================================================================
-- RÉSUMÉ DES COMPTES CRÉÉS:
-- ===================================================================
-- 
-- ADMIN (Accès dashboard ✅)
--   Email:    admin@tahwissa.com
--   Password: admin123
--   Rôle:     ADMIN (role_id = 3)
-- 
-- AGENT (Accès dashboard ✅)
--   Email:    agent@tahwissa.com
--   Password: agent123
--   Rôle:     AGENT (role_id = 2)
-- 
-- USER (Accès dashboard ❌)
--   Email:    user@tahwissa.com
--   Password: user123
--   Rôle:     USER (role_id = 1)
-- 
-- ===================================================================

-- ═══════════════════════════════════════════════════════════════════════
-- CRÉATION D'UN UTILISATEUR TEST (RÔLE USER)
-- Pour tester la page "Coming Soon"
-- ═══════════════════════════════════════════════════════════════════════

USE tahwissa_db;

-- Supprimer l'utilisateur test s'il existe déjà
DELETE FROM users WHERE email = 'user@tahwissa.com';

-- Créer un utilisateur normal (rôle USER)
INSERT INTO users (email, password, first_name, last_name, phone, role, is_verified, is_active, city, country, created_at)
VALUES (
    'user@tahwissa.com',           -- Email
    'user123',                      -- Mot de passe (en clair pour les tests)
    'Mohamed',                      -- Prénom
    'Ben Ali',                      -- Nom
    '+216 20 123 456',              -- Téléphone
    'USER',                         -- Rôle: USER (utilisateur normal)
    TRUE,                           -- Vérifié
    TRUE,                           -- Actif
    'Tunis',                        -- Ville
    'Tunisie',                      -- Pays
    NOW()                           -- Date de création
);

-- Afficher le résultat
SELECT 
    id,
    email,
    password,
    first_name,
    last_name,
    role,
    is_verified,
    is_active,
    city,
    country
FROM users
WHERE email = 'user@tahwissa.com';

-- ═══════════════════════════════════════════════════════════════════════
-- INFORMATIONS DE CONNEXION
-- ═══════════════════════════════════════════════════════════════════════
-- Email:    user@tahwissa.com
-- Password: user123
-- Rôle:     USER
-- 
-- Après connexion → Redirigé vers la page "Coming Soon"
-- ═══════════════════════════════════════════════════════════════════════

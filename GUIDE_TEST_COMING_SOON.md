# 🧪 GUIDE DE TEST - PAGE "COMING SOON"

## 📋 PRÉPARATION

### 1. Créer un utilisateur test USER dans la base de données

**Méthode 1: Utiliser le script SQL**
```sql
-- Exécuter le fichier: create_user_test.sql
-- Ou copier-coller ce code dans MySQL Workbench:

USE tahwissa_db;

DELETE FROM users WHERE email = 'user@tahwissa.com';

INSERT INTO users (email, password, first_name, last_name, phone, role, is_verified, is_active, city, country, created_at)
VALUES (
    'user@tahwissa.com',
    'user123',
    'Mohamed',
    'Ben Ali',
    '+216 20 123 456',
    'USER',
    TRUE,
    TRUE,
    'Tunis',
    'Tunisie',
    NOW()
);
```

**Méthode 2: Depuis l'interface (si disponible)**
- Aller sur "Gestion des utilisateurs"
- Cliquer sur "Ajouter"
- Remplir les informations avec rôle = "USER"

---

## 🚀 COMPILATION

```powershell
# Nettoyer et compiler le projet
mvn clean compile

# Lancer l'application
mvn javafx:run
```

Ou simplement double-cliquer sur: **run.bat**

---

## ✅ SCÉNARIOS DE TEST

### 🔹 TEST 1: Connexion ADMIN → Dashboard

**Étapes:**
1. Lancer l'application
2. Se connecter avec:
   - Email: `admin@tahwissa.com`
   - Password: `admin123`
3. Cliquer sur "Connexion"

**Résultat attendu:**
- ✅ Message: "✅ Connexion réussie! Bienvenue Mohamed"
- ✅ Redirection vers le **Dashboard**
- ✅ Dashboard affiche "✅ Accès autorisé"
- ✅ Options: Gestion des utilisateurs, Statistiques, etc.

**Console attendue:**
```
✅ Authentification réussie!
🔍 Vérification du rôle: ADMIN
🔑 Rôle ADMIN/AGENT → Dashboard
🔑 Session: Utilisateur connecté - admin@tahwissa.com (ADMIN)
✅ Dashboard chargé avec le rôle: ADMIN
```

---

### 🔹 TEST 2: Connexion USER → Coming Soon Page

**Étapes:**
1. Depuis le Dashboard (si connecté), cliquer sur "Déconnexion"
2. OU relancer l'application
3. Se connecter avec:
   - Email: `user@tahwissa.com`
   - Password: `user123`
4. Cliquer sur "Connexion"

**Résultat attendu:**
- ✅ Message: "✅ Connexion réussie! Bienvenue Mohamed"
- ✅ Redirection vers la **page Coming Soon**
- ✅ Page affiche:
  - 🚀 Logo avec fusée et globe
  - 📝 "Bientôt Disponible!" / "We Are Coming Soon"
  - 👤 "Bienvenue, Mohamed Ben Ali !"
  - 📧 "user@tahwissa.com"
  - 📋 "Rôle: Voyageur"
  - 🎯 3 cartes de fonctionnalités
  - 🔘 Boutons: Déconnexion, Être notifié

**Console attendue:**
```
✅ Authentification réussie!
🔍 Vérification du rôle: USER
👤 Rôle USER → Page Coming Soon
🚀 Chargement de la page Coming Soon...
🔑 Session: Utilisateur connecté - user@tahwissa.com (USER)
✅ Page Coming Soon chargée
✅ ComingSoonController initialisé
👤 Utilisateur: user@tahwissa.com
✅ Page Coming Soon affichée pour: Mohamed
```

---

### 🔹 TEST 3: Navigation Dashboard ↔ Liste (ADMIN)

**Étapes:**
1. Se connecter en tant qu'ADMIN
2. Depuis le Dashboard, cliquer sur "Gestion des utilisateurs"
3. Vérifier que la liste s'affiche
4. Cliquer sur "Retour au Dashboard"

**Résultat attendu:**
- ✅ Navigation fluide
- ✅ Dashboard affiche toujours "✅ Accès autorisé"
- ✅ Pas de message d'erreur
- ✅ Rôle ADMIN conservé

**Console attendue:**
```
🔙 Retour au dashboard...
🔍 Vérification de la session avant retour...
✅ DashboardController initialisé
🔑 Rôle récupéré depuis la session: ADMIN
```

---

### 🔹 TEST 4: Déconnexion depuis Coming Soon

**Étapes:**
1. Se connecter en tant qu'USER
2. Sur la page Coming Soon, cliquer sur "🚪 Déconnexion"

**Résultat attendu:**
- ✅ Retour immédiat à la page de login
- ✅ Champs email et password vides
- ✅ Session nettoyée

**Console attendue:**
```
🚪 Déconnexion depuis la page Coming Soon...
🚪 Session: Déconnexion de user@tahwissa.com
✅ Retour à la page de connexion
```

---

### 🔹 TEST 5: Reconnexion après déconnexion

**Étapes:**
1. Se déconnecter (ADMIN ou USER)
2. Se reconnecter avec le même compte

**Résultat attendu:**
- ✅ Connexion réussie
- ✅ Redirection correcte selon le rôle
- ✅ Informations utilisateur affichées

---

### 🔹 TEST 6: Tentative de connexion avec identifiants incorrects

**Étapes:**
1. Essayer de se connecter avec:
   - Email: `wrong@email.com`
   - Password: `wrongpass`

**Résultat attendu:**
- ✅ Message d'erreur: "❌ Email ou mot de passe incorrect"
- ✅ Pas de redirection
- ✅ Reste sur la page de login

---

## 🎨 VÉRIFICATIONS VISUELLES

### Page Coming Soon - Éléments à vérifier:

#### 🎯 Layout
- [ ] Dégradé de fond bleu visible (du foncé au clair)
- [ ] Cercles décoratifs en arrière-plan
- [ ] Tous les éléments centrés

#### 📝 Texte
- [ ] Titre "Bientôt Disponible!" en grand et gras
- [ ] Sous-titre "We Are Coming Soon" en italique
- [ ] Description lisible
- [ ] Nom de l'utilisateur affiché correctement
- [ ] Email affiché correctement
- [ ] "Rôle: Voyageur" visible

#### 🎴 Cartes de fonctionnalités
- [ ] 3 cartes visibles côte à côte
- [ ] Icônes: ✈️ (Vols), 🏨 (Hébergement), 🗺️ (Itinéraires)
- [ ] Fond semi-transparent
- [ ] Texte lisible

#### 🔘 Boutons
- [ ] Bouton "Déconnexion" avec bordure blanche
- [ ] Bouton "Être notifié du lancement" en orange
- [ ] Effet hover sur les boutons (si défini dans CSS)

#### 📧 Footer
- [ ] Texte de contact visible
- [ ] Email et téléphone affichés

---

## 🐛 PROBLÈMES POSSIBLES & SOLUTIONS

### ❌ Problème: L'utilisateur USER voit le Dashboard

**Cause:** Le rôle n'est pas "USER" dans la base de données

**Solution:**
```sql
-- Vérifier le rôle dans la BD:
SELECT email, role FROM users WHERE email = 'user@tahwissa.com';

-- Si le rôle n'est pas USER, le corriger:
UPDATE users SET role = 'USER' WHERE email = 'user@tahwissa.com';
```

---

### ❌ Problème: Page Coming Soon ne charge pas

**Cause:** Fichier FXML introuvable ou contrôleur non compilé

**Solution:**
```powershell
# Recompiler le projet
mvn clean compile

# Vérifier que les fichiers existent:
# - src/main/resources/view/coming-soon.fxml
# - src/main/java/controller/ComingSoonController.java
```

---

### ❌ Problème: Nom d'utilisateur ne s'affiche pas

**Cause:** L'utilisateur n'est pas enregistré dans SessionManager

**Solution:**
- Vérifier dans la console: "🔑 Session: Utilisateur connecté"
- Si absent, vérifier LoginController.openComingSoon()
- S'assurer que SessionManager.setCurrentUser(user) est appelé

---

### ❌ Problème: Erreur au retour Dashboard après liste

**Cause:** SessionManager non utilisé (problème résolu normalement)

**Solution:**
- Vérifier que SessionManager.getInstance().getCurrentRole() est appelé
- Recompiler le projet

---

## 📊 MATRICE DE TEST

| Test | Rôle | Action | Résultat attendu | Statut |
|------|------|--------|------------------|--------|
| 1 | ADMIN | Login | Dashboard | ⬜ |
| 2 | USER | Login | Coming Soon | ⬜ |
| 3 | AGENT | Login | Dashboard | ⬜ |
| 4 | ADMIN | Dashboard → Liste | Liste affichée | ⬜ |
| 5 | ADMIN | Liste → Dashboard | Accès maintenu | ⬜ |
| 6 | USER | Déconnexion | Retour login | ⬜ |
| 7 | ADMIN | Déconnexion | Retour login | ⬜ |
| 8 | - | Login incorrect | Message erreur | ⬜ |

**Légende:** ⬜ À tester | ✅ Réussi | ❌ Échoué

---

## 📸 CAPTURES D'ÉCRAN ATTENDUES

### 1. Page Coming Soon
```
┌──────────────────────────────────────────────────────────┐
│  [Fond dégradé bleu: #1e3c72 → #3498db]                 │
│                                                          │
│               🚀                                         │
│               🌍                                         │
│                                                          │
│         Bientôt Disponible!                              │
│       We Are Coming Soon                                 │
│                                                          │
│  L'espace utilisateur Tahwissa est en cours...          │
│                                                          │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐               │
│   │   ✈️    │  │   🏨    │  │   🗺️    │               │
│   │Réservation│ │Hébergement│ │Itinéraires│            │
│   └─────────┘  └─────────┘  └─────────┘               │
│                                                          │
│        👤 Bienvenue, Mohamed Ben Ali !                  │
│           user@tahwissa.com                              │
│              Rôle: Voyageur                              │
│                                                          │
│  [🚪 Déconnexion]  [🔔 Être notifié du lancement]      │
│                                                          │
│      📧 contact@tahwissa.com | 📞 +216 XX XXX XXX      │
└──────────────────────────────────────────────────────────┘
```

---

## ✅ CHECKLIST FINALE

Avant de valider que tout fonctionne:

- [ ] Compilation réussie (mvn clean compile)
- [ ] Utilisateur USER créé dans la BD
- [ ] Utilisateur ADMIN existe dans la BD
- [ ] Connexion ADMIN → Dashboard fonctionne
- [ ] Connexion USER → Coming Soon fonctionne
- [ ] Informations utilisateur affichées sur Coming Soon
- [ ] Déconnexion depuis Coming Soon fonctionne
- [ ] Déconnexion depuis Dashboard fonctionne
- [ ] Navigation Dashboard ↔ Liste maintient le rôle ADMIN
- [ ] Design de la page Coming Soon conforme
- [ ] Pas d'erreurs dans la console
- [ ] Tous les boutons cliquables
- [ ] Texte lisible (contraste suffisant)

---

## 🎯 RÉSULTAT FINAL ATTENDU

✅ **Utilisateurs ADMIN/AGENT** → Dashboard complet avec gestion  
✅ **Utilisateurs USER** → Page élégante "Coming Soon"  
✅ **Session maintenue** dans toute l'application  
✅ **Déconnexion propre** depuis toutes les pages  
✅ **Design cohérent** avec le thème Tahwissa  

---

**Date de création:** 15 février 2026  
**Version:** 1.0  
**Statut:** Prêt pour les tests


# 🔒 Vérification Biométrique - Désactivée Temporairement

## ℹ️ État actuel

La fonctionnalité de vérification biométrique a été **commentée temporairement** pour permettre de travailler sur d'autres aspects du projet.

## 📁 Fichiers concernés

### 1. **PythonBiometricService.java**
- **Chemin** : `src/main/java/services/PythonBiometricService.java`
- **État** : ✅ Entièrement commenté
- **Description** : Service qui exécute le script Python de détection faciale
- **Fonctionnalités** :
  - Vérification via webcam
  - Vérification d'une image existante
  - Détection de visages humains avec OpenCV
  - Installation automatique des dépendances Python

### 2. **LoginController.java**
- **Chemin** : `src/main/java/controller/LoginController.java`
- **État** : ✅ Partiellement commenté
- **Modifications** :
  - Import de `PythonBiometricService` commenté
  - Déclaration du service commentée
  - Initialisation du service commentée
  - Section de vérification biométrique dans `handleRegister()` commentée
  - **Le reste du code fonctionne normalement** (login, inscription sans vérification)

### 3. **human_verification.py**
- **Chemin** : `scripts/human_verification.py`
- **État** : ✅ Fichier intact (non modifié)
- **Description** : Script Python avec OpenCV pour détecter les visages
- **Note** : Prêt à être réactivé quand nécessaire

## 🔄 Comment ça fonctionne maintenant ?

### Avant (avec vérification biométrique)
```
Inscription → Validation des champs → 🎥 Webcam (OpenCV) → Détection faciale → Création du compte
```

### Maintenant (sans vérification biométrique)
```
Inscription → Validation des champs → Création du compte (directe)
```

## ⚙️ Comportement actuel

✅ **Fonctionnalités actives** :
- Login avec email/password
- Inscription avec tous les champs (nom, prénom, email, téléphone, ville, pays, mot de passe)
- Validation stricte des champs
- Redirection selon le rôle (Admin/Agent → Dashboard, User → Coming Soon)
- Connexion à la base de données

❌ **Fonctionnalités désactivées** :
- Vérification faciale via webcam lors de l'inscription
- Détection de visages humains avec OpenCV
- Appel au script Python `human_verification.py`

## 🔧 Pour réactiver la vérification biométrique

### Étape 1 : Décommenter PythonBiometricService.java
```java
// Supprimer les marques de commentaire /* et */
// Au début et à la fin du fichier
```

### Étape 2 : Décommenter dans LoginController.java
```java
// Ligne 4 : Décommenter l'import
import services.PythonBiometricService;

// Ligne 39-40 : Décommenter les déclarations
private PythonBiometricService biometricService;
private boolean biometricVerified = false;

// Ligne 46 : Décommenter l'initialisation
biometricService = new PythonBiometricService();

// Lignes 380-500 : Décommenter toute la section de vérification biométrique
// ET commenter la ligne de création directe du compte
```

### Étape 3 : Installer OpenCV (Python)
```bash
cd C:\Users\mohamed\Downloads\workshopA6\workshopA6
python -m pip install opencv-python
```

### Étape 4 : Tester le script Python
```bash
cd C:\Users\mohamed\Downloads\workshopA6\workshopA6
python scripts/human_verification.py webcam 10
```

## 📦 Dépendances requises (pour réactivation)

### Python
- **Python 3.x** (testé avec Python 3.11+)
- **opencv-python** : Détection faciale
- **numpy** : Traitement des images (dépendance d'OpenCV)

### Java
- **Gson** : Pour parser le JSON retourné par Python
- Déjà inclus dans `pom.xml`

## 📝 Notes importantes

1. ⚠️ **Le code commenté est fonctionnel** - Il suffit de le décommenter pour le réactiver
2. 📸 **Le script Python est prêt** - `human_verification.py` est complet et testé
3. 🔄 **L'inscription fonctionne** - Les utilisateurs peuvent s'inscrire sans problème
4. 🛡️ **Sécurité** - La vérification biométrique empêche les bots, mais n'est pas critique pour le développement

## 🎯 Pourquoi l'avoir désactivé ?

- Pour simplifier les tests et le développement
- Pour éviter les dépendances Python pendant le développement d'autres fonctionnalités
- Pour permettre des inscriptions rapides sans webcam
- Pour se concentrer sur d'autres aspects de l'application

## ✅ Prochaines étapes recommandées

1. **Terminer les autres fonctionnalités** (dashboard, gestion des utilisateurs, etc.)
2. **Réactiver la vérification biométrique** avant la production
3. **Tester avec différentes webcams** et conditions d'éclairage
4. **Ajouter une option** pour désactiver temporairement la vérification (mode développement)

---

📅 **Date de désactivation** : 16 février 2026  
👤 **Modifié par** : GitHub Copilot  
🔖 **Version** : Tahwissa v1.0-SNAPSHOT


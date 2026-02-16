# 🔐 Guide de Configuration - Vérification Biométrique Tahwissa

## 📋 Prérequis

### 1. Python 3.x
La vérification biométrique nécessite **Python 3.7 ou supérieur**.

#### Vérifier l'installation de Python :
```powershell
python --version
```

#### Télécharger Python (si nécessaire) :
🔗 https://www.python.org/downloads/

⚠️ **Important** : Cocher **"Add Python to PATH"** lors de l'installation !

---

## 📦 Installation des Dépendances

### Méthode 1 : Installation automatique via pip

Ouvrir PowerShell dans le dossier du projet et exécuter :

```powershell
pip install opencv-python
```

### Méthode 2 : Installer toutes les dépendances depuis requirements.txt

```powershell
pip install -r requirements.txt
```

---

## 🧪 Tester le Script Python

### Test en mode webcam (15 secondes)
```powershell
cd scripts
python human_verification.py webcam 15
```

**Instructions** :
- 📹 Positionnez votre visage face à la caméra
- ⌛ Attendez que le message "✅ Visage détecté" apparaisse
- ⌨️ Appuyez sur **ESPACE** pour capturer
- 🚪 Appuyez sur **ESC** pour annuler

### Test avec une image existante
```powershell
python human_verification.py image "chemin/vers/image.jpg"
```

---

## 🚀 Utilisation dans l'Application JavaFX

### Processus d'inscription avec vérification biométrique :

1. **L'utilisateur remplit le formulaire d'inscription**
   - Prénom, Nom, Email, Téléphone, Ville, Pays, Mot de passe

2. **Validation des champs**
   - Toutes les données sont vérifiées (format email, longueur mot de passe, etc.)

3. **Lancement de la vérification biométrique** 🔐
   - Une fenêtre OpenCV s'ouvre automatiquement
   - La webcam s'active
   - L'algorithme détecte les visages en temps réel

4. **Capture et vérification**
   - L'utilisateur se positionne face à la caméra
   - Un rectangle violet entoure le visage détecté
   - L'utilisateur appuie sur ESPACE pour valider

5. **Création du compte** ✅
   - Si un visage unique est détecté → Compte créé
   - Si aucun visage ou plusieurs visages → Erreur, réessayer

---

## 🛡️ Sécurité et Confidentialité

### Garanties de protection :

✅ **Pas de stockage des images** : Les photos capturées ne sont jamais sauvegardées sur le serveur

✅ **Traitement local** : L'analyse faciale se fait uniquement sur l'ordinateur de l'utilisateur

✅ **Détection anti-bot** : Empêche les inscriptions automatisées par des scripts malveillants

✅ **Visage humain requis** : Seuls les vrais visages humains sont acceptés (pas de photos d'écran)

---

## 🔧 Dépannage

### ❌ Erreur : "Python n'est pas installé"

**Solution** :
1. Installer Python depuis https://www.python.org/downloads/
2. Cocher "Add Python to PATH"
3. Redémarrer l'application

### ❌ Erreur : "Impossible d'accéder à la webcam"

**Solutions** :
- Vérifier que la webcam est connectée
- Vérifier les permissions de la webcam dans Windows
- Fermer les autres applications utilisant la webcam (Teams, Zoom, etc.)

### ❌ Erreur : "No module named 'cv2'"

**Solution** :
```powershell
pip install opencv-python
```

### ❌ Erreur : "Aucun visage détecté"

**Solutions** :
- Améliorer l'éclairage de la pièce
- Se rapprocher de la caméra
- Regarder directement la caméra
- Retirer les lunettes de soleil / masque

### ❌ Erreur : "Plusieurs visages détectés"

**Solution** :
- S'assurer d'être seul(e) face à la caméra
- Éviter les affiches/photos avec des visages en arrière-plan

---

## 📊 Architecture Technique

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION JAVAFX                        │
│                  (LoginController.java)                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Appel
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              PythonBiometricService.java                     │
│         • Exécute le script Python                           │
│         • Parse les résultats JSON                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ ProcessBuilder
                         ▼
┌─────────────────────────────────────────────────────────────┐
│            scripts/human_verification.py                     │
│         • Ouvre la webcam (OpenCV)                           │
│         • Détecte les visages (Haar Cascade)                 │
│         • Retourne JSON: {success, message, face_count}      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📚 Fichiers Concernés

### Java
- `LoginController.java` - Gère l'inscription et lance la vérification
- `PythonBiometricService.java` - Exécute le script Python
- `pom.xml` - Dépendance Gson pour parser JSON

### Python
- `scripts/human_verification.py` - Script de détection faciale

### Configuration
- `requirements.txt` - Dépendances Python (opencv-python)

---

## ✨ Fonctionnalités Implémentées

✅ Détection faciale en temps réel avec OpenCV

✅ Interface utilisateur guidée avec instructions

✅ Validation : exactement 1 visage requis

✅ Feedback visuel : rectangles autour des visages détectés

✅ Capture manuelle (ESPACE) ou automatique (30% des frames)

✅ Messages d'erreur explicites

✅ Fallback : possibilité de continuer sans vérification si Python manquant

✅ Palette de couleurs Tahwissa : Purple, Indigo, Blue

---

## 🎨 Palette de Couleurs

```css
Purple: #9333EA
Indigo: #4F46E5
Blue:   #3B82F6
```

---

## 📞 Support

Pour toute question ou problème :
- 📧 Email : support@tahwissa.com
- 📞 Téléphone : +216 XX XXX XXX
- 🌐 Site web : www.tahwissa.com

---

## 📝 Notes de Version

**Version 1.0** (Février 2026)
- ✨ Première implémentation de la vérification biométrique
- 🔐 Protection anti-bot lors de l'inscription
- 🎥 Interface webcam avec détection en temps réel
- 📊 Intégration Java ↔ Python via JSON

---

**Développé avec ❤️ par l'équipe Tahwissa** ✈️🗺️💬


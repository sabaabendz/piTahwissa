# 🚀 DÉMARRAGE RAPIDE - Vérification Biométrique Tahwissa

## ⚡ En 3 minutes chrono !

### Étape 1 : Vérifier Python (30 secondes)
```powershell
python --version
```

**✅ Si vous voyez "Python 3.x.x"** → Passez à l'étape 2

**❌ Si erreur** → Installez Python :
1. Téléchargez : https://www.python.org/downloads/
2. ⚠️ COCHEZ "Add Python to PATH" !
3. Installez
4. Redémarrez PowerShell
5. Retestez `python --version`

---

### Étape 2 : Installer OpenCV (1 minute)
```powershell
# Ouvrez PowerShell dans le dossier du projet
cd C:\Users\mohamed\Downloads\workshopA6\workshopA6

# Lancez le script d'installation automatique
.\install_python_deps.bat
```

**OU**

```powershell
pip install opencv-python
```

---

### Étape 3 : Tester le script Python (30 secondes)
```powershell
cd scripts
python human_verification.py webcam 10
```

**Instructions** :
- 📹 Positionnez votre visage dans le cadre
- ⏳ Attendez "✅ Visage détecté"
- ⌨️ Appuyez sur **ESPACE** pour capturer
- 🎉 Si succès → JSON avec `"success": true`

---

### Étape 4 : Lancer l'application JavaFX (1 minute)
```powershell
cd ..
.\run.bat
```

**OU**

```powershell
mvn javafx:run
```

---

## 🧪 TEST COMPLET

1. **Cliquez sur "S'inscrire"**
2. **Remplissez le formulaire** :
   - Prénom : Mohamed
   - Nom : Test
   - Email : mohamed.test@tahwissa.com
   - Téléphone : +216 12 345 678
   - Ville : Tunis
   - Pays : Tunisie
   - Mot de passe : test123
   - Rôle : Voyageur

3. **Cliquez "Créer mon compte"**

4. **Fenêtre OpenCV s'ouvre** 🎥
   - Positionnez votre visage
   - Attendez le rectangle violet
   - ESPACE pour capturer

5. **Compte créé !** ✅

---

## 🐛 PROBLÈMES COURANTS

### ❌ "Python n'est pas reconnu"
**Solution** : Ajoutez Python au PATH
```powershell
# Trouvez où Python est installé
where python

# Ajoutez au PATH (exemple)
$env:Path += ";C:\Users\mohamed\AppData\Local\Programs\Python\Python312"
```

### ❌ "No module named 'cv2'"
**Solution** :
```powershell
pip install opencv-python
```

### ❌ "Webcam non détectée"
**Solutions** :
- Vérifier que la webcam est connectée
- Fermer Teams/Zoom/Skype
- Windows → Paramètres → Confidentialité → Caméra → Autoriser

### ❌ "Aucun visage détecté"
**Solutions** :
- Améliorer l'éclairage
- Se rapprocher de la caméra
- Regarder directement la caméra
- Retirer lunettes de soleil

---

## 📋 CHECKLIST AVANT DE COMMENCER

- [ ] Python 3.7+ installé
- [ ] Python dans le PATH
- [ ] `pip install opencv-python` exécuté
- [ ] Webcam fonctionnelle
- [ ] MySQL en cours d'exécution
- [ ] Base de données `pidev` créée
- [ ] Java 21 installé
- [ ] Maven configuré

---

## 🎯 COMMANDES UTILES

```powershell
# Vérifier Python
python --version

# Vérifier pip
pip --version

# Installer OpenCV
pip install opencv-python

# Tester le script
python scripts/human_verification.py webcam 10

# Compiler le projet
mvn clean compile

# Lancer l'application
mvn javafx:run

# OU
.\run.bat
```

---

## 📞 BESOIN D'AIDE ?

Consultez :
- 📖 **GUIDE_VERIFICATION_BIOMETRIQUE.md** - Documentation complète
- 📊 **INTEGRATION_BIOMETRIQUE_RESUME.md** - Résumé technique

---

**Bon développement ! 🚀**


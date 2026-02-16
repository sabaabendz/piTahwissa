# 🎯 PROBLÈME D'AJOUT D'UTILISATEUR - RÉSOLU

## ✅ CORRECTIONS EFFECTUÉES

### 1. **LoginController.java** - Ligne 188
**AVANT (ERREUR):**
```java
newUser.setPhoneNumber(phoneNumberField.getText().trim());  // ❌ Méthode inexistante
```

**APRÈS (CORRIGÉ):**
```java
newUser.setPhone(phoneNumberField.getText().trim());  // ✅ Méthode correcte
```

### 2. **LoginController.java** - Ligne 193
**AVANT (ERREUR):**
```java
userService.create(newUser);  // ❌ Méthode inexistante
```

**APRÈS (CORRIGÉ):**
```java
userService.ajouter(newUser);  // ✅ Méthode correcte
```

### 3. **UserService.java** - Méthode ajouter()
**AVANT (INCOMPLET):**
```java
String sql = "INSERT INTO user (email, password, first_name, last_name, role_id) VALUES (?, ?, ?, ?, ?)";
// ❌ Manque: phone, city, country, is_verified, is_active
```

**APRÈS (COMPLET):**
```java
String sql = "INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

ps.setString(1, user.getEmail());
ps.setString(2, user.getPassword());
ps.setString(3, user.getFirstName());
ps.setString(4, user.getLastName());
ps.setString(5, user.getPhone());           // ✅ AJOUTÉ
ps.setString(6, user.getCity());            // ✅ AJOUTÉ
ps.setString(7, user.getCountry());         // ✅ AJOUTÉ
ps.setInt(8, getRoleId(user.getRole()));
ps.setBoolean(9, user.isVerified());        // ✅ AJOUTÉ
ps.setBoolean(10, user.isActive());         // ✅ AJOUTÉ
```

### 4. **UserService.java** - Méthode getRoleId()
**AMÉLIORATION:**
```java
return switch (roleName.toUpperCase()) {
    case "USER", "VOYAGEUR" -> 1;  // ✅ Support "Voyageur"
    case "AGENT", "GUIDE" -> 2;    // ✅ Support "Guide"
    case "ADMIN" -> 3;
    default -> 1;
};
```

---

## 📋 VÉRIFICATION BASE DE DONNÉES

La table `user` doit avoir ces colonnes:

```sql
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
```

Si des colonnes manquent, exécutez:
```sql
ALTER TABLE user ADD COLUMN phone VARCHAR(20);
ALTER TABLE user ADD COLUMN city VARCHAR(100);
ALTER TABLE user ADD COLUMN country VARCHAR(100);
ALTER TABLE user ADD COLUMN is_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE user ADD COLUMN is_active BOOLEAN DEFAULT TRUE;
```

---

## 🚀 COMMENT TESTER

### Option 1: Via IntelliJ IDEA (RECOMMANDÉ)
1. Ouvrir le projet dans IntelliJ IDEA
2. Clic droit sur `Main.java`
3. Sélectionner "Run 'Main.main()'"

### Option 2: Via Maven (si Maven est installé)
```powershell
cd C:\Users\mohamed\Downloads\workshopA6\workshopA6
mvn clean compile
mvn javafx:run
```

### Option 3: Via le fichier batch créé
Double-cliquer sur `run.bat` dans le dossier du projet

---

## 📊 FLUX D'INSCRIPTION COMPLET

1. ✅ Utilisateur clique sur "S'inscrire" dans login.fxml
2. ✅ Formulaire d'inscription apparaît avec 9 champs:
   - Prénom
   - Nom
   - Email
   - Téléphone
   - Ville
   - Pays
   - Mot de passe
   - Confirmation mot de passe
   - Rôle (Voyageur/Guide/Agent)

3. ✅ Validations effectuées:
   - Tous les champs sont remplis
   - Email valide (contient @ et .)
   - Mot de passe ≥ 6 caractères
   - Les 2 mots de passe correspondent

4. ✅ Si valide:
   - Création d'un objet User avec TOUTES les données
   - Appel à `userService.ajouter(newUser)`
   - Insertion dans la base de données MySQL
   - Message de succès
   - Retour automatique au mode connexion après 2 secondes

---

## ⚠️ MESSAGES D'ERREUR POSSIBLES

| Message | Cause | Solution |
|---------|-------|----------|
| "❌ Veuillez remplir tous les champs" | Champ vide | Remplir tous les champs |
| "❌ Email invalide" | Format email incorrect | Utiliser format valide (ex@exemple.com) |
| "❌ Le mot de passe doit contenir au moins 6 caractères" | Mot de passe trop court | Utiliser ≥6 caractères |
| "❌ Les mots de passe ne correspondent pas" | Mots de passe différents | Ressaisir le même mot de passe |
| "❌ Erreur lors de l'inscription: ..." | Erreur SQL | Vérifier la structure de la table |

---

## 🔍 DEBUG SI PROBLÈME PERSISTE

Ajoutez ces prints dans `LoginController.handleRegister()`:

```java
System.out.println("🔍 DEBUG - Inscription:");
System.out.println("  - Email: " + email);
System.out.println("  - Téléphone: " + phoneNumberField.getText().trim());
System.out.println("  - Ville: " + cityField.getText().trim());
System.out.println("  - Pays: " + countryField.getText().trim());
System.out.println("  - Rôle: " + roleChoiceBox.getValue());
```

Et dans `UserService.ajouter()`, vérifier les valeurs avant l'insertion.

---

## ✅ STATUT FINAL

- ✅ Corrections appliquées aux fichiers
- ✅ Méthodes corrigées (setPhone, ajouter)
- ✅ Requête SQL complète avec tous les champs
- ✅ Validation complète des données
- ✅ Gestion d'erreurs améliorée
- ⚠️ À tester: Compilation et exécution (nécessite Maven ou IDE)

**L'ajout d'utilisateur devrait maintenant fonctionner correctement!** 🎉


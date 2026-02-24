# Tahwissa - Face Login (Setup rapide)

## 1) Base de donnees
Ajoutez les colonnes dans la table `user`:

```sql
ALTER TABLE user
  ADD COLUMN face_embedding LONGBLOB NULL,
  ADD COLUMN face_enrolled_at TIMESTAMP NULL;
```

## 2) Python (Windows)
Installez les dependances:

```powershell
C:\Users\mohamed\Downloads\workshopA6\workshopA6\venv_tahwissa\Scripts\python.exe -m pip install -r requirements.txt
```

Test rapide:

```powershell
C:\Users\mohamed\Downloads\workshopA6\workshopA6\venv_tahwissa\Scripts\python.exe scripts\face_recognition_smoke_test.py
```

## 3) Utilisation
- Inscription: capture du visage pendant l'inscription (embedding stocke en DB).
- Connexion: bouton "Se connecter par visage" pour comparer l'embedding live avec celui stocke.

## Notes
- `face_recognition` peut exiger des outils de compilation (dlib). Si l'installation echoue, essayez une version pre-compilee compatible Windows.
- Le login par mot de passe reste disponible comme fallback.


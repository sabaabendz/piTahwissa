# 🎯 Base de Données Complète

## Description
Combine reservation + événements

## Installation
```bash
mysql -u root -p < full-schema.sql
```

## Modules
- Reservation (2 tables)
- Événements (4 tables)
```

---

## 🎯 **ÉTAPE 4: VÉRIFIER LA STRUCTURE**

**Votre projet doit ressembler à ça:**
```
GestionReservation/
├── src/
├── target/
├── pom.xml
└── database/
    ├── reserv/
    │   ├── schema.sql          ← Votre base exportée
    │   └── README.md
    ├── evenement/
    │   ├── schema.sql          ← Base événements
    │   └── README.md
    └── reserevenement/
        ├── full-schema.sql     ← Les deux combinés
        └── README.md
```

---

## 🎯 **ÉTAPE 5: OUVRIR GIT BASH**

1. **Clic droit** dans votre dossier projet:
```
   C:\Users\sabso\IdeaProjects\pidevproject\GestionReservation\
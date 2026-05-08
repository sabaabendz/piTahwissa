-- ============================================================
-- Tahwissa - Client Space Database Schema
-- Table: paiement (Payments)
-- ============================================================

CREATE TABLE IF NOT EXISTS paiement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_reservation INT NOT NULL,
    id_utilisateur INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
    methode_paiement VARCHAR(50) NOT NULL DEFAULT 'CARTE' COMMENT 'CARTE, VIREMENT, ESPECES, PAYPAL',
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE' COMMENT 'PAYE, EN_ATTENTE, REMBOURSE, ECHOUE',
    reference VARCHAR(100) UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_reservation) REFERENCES reservationvoyage(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================
-- Sample data for testing
-- ============================================================

-- Insert sample payments (adjust id_reservation and id_utilisateur to match your data)
INSERT INTO paiement (id_reservation, id_utilisateur, montant, date_paiement, methode_paiement, statut, reference) VALUES
(1, 1, 1500.00, '2026-01-15 10:30:00', 'CARTE', 'PAYE', 'PAY-20260115-001'),
(2, 1, 2200.00, '2026-01-20 14:15:00', 'VIREMENT', 'PAYE', 'PAY-20260120-002'),
(3, 1, 850.00, '2026-02-01 09:45:00', 'PAYPAL', 'EN_ATTENTE', 'PAY-20260201-003'),
(4, 1, 3000.00, '2026-02-10 16:00:00', 'CARTE', 'PAYE', 'PAY-20260210-004'),
(5, 1, 1200.00, '2026-02-15 11:20:00', 'ESPECES', 'EN_ATTENTE', 'PAY-20260215-005');

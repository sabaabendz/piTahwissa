-- View: available places per event (for reference / reports)
-- Reserved = CONFIRMEE + EN_ATTENTE only; ANNULEE does not reduce capacity.
-- Run after evenementbd.sql: USE evenementbd; source available_places_view.sql;

USE evenementbd;

CREATE OR REPLACE VIEW v_evenement_places_disponibles AS
SELECT
  e.id_evenement,
  e.titre,
  e.nb_places AS nb_places_total,
  COALESCE(SUM(CASE WHEN r.statut IN ('CONFIRMEE', 'EN_ATTENTE') THEN r.nb_places_reservees ELSE 0 END), 0) AS nb_places_reservees,
  GREATEST(0, e.nb_places - COALESCE(SUM(CASE WHEN r.statut IN ('CONFIRMEE', 'EN_ATTENTE') THEN r.nb_places_reservees ELSE 0 END), 0)) AS nb_places_disponibles
FROM evenement e
LEFT JOIN reservation_evenement r ON r.id_evenement = e.id_evenement
GROUP BY e.id_evenement, e.titre, e.nb_places;

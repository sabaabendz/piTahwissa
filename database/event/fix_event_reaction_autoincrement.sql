-- Fix: id_reaction must be AUTO_INCREMENT so INSERT without id_reaction works.
-- Run on the DB used by the app (e.g. gestionreservation):
--   USE gestionreservation;
--   source fix_event_reaction_autoincrement.sql;

ALTER TABLE evenement_reaction
  MODIFY COLUMN id_reaction INT(11) NOT NULL AUTO_INCREMENT;

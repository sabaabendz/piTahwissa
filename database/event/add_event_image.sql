-- Add image support for events (evenementbd)
-- Run in MySQL: USE evenementbd; then paste/source this script.

ALTER TABLE evenement ADD COLUMN image_filename VARCHAR(255) NULL DEFAULT NULL AFTER statut;

ALTER TABLE user
  ADD COLUMN face_embedding LONGBLOB NULL,
  ADD COLUMN face_enrolled_at TIMESTAMP NULL;


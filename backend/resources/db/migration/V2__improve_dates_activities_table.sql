ALTER TABLE api.activities
RENAME COLUMN date TO created_at;

ALTER TABLE api.activities
ADD COLUMN updated_at TIMESTAMP;

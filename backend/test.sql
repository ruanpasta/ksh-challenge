SELECT * FROM api.activities where updated_at is not null limit 2;

SELECT COUNT(*) FROM api.activities;

SELECT MAX(planned_amount) AS max_planned_amount FROM api.activities;

SELECT MAX(executed_amount) AS max_executed_amount FROM api.activities;

SELECT * FROM api.activities
WHERE ('2025-07-18' IS NULL OR created_at = DATE '2025-07-19')
LIMIT 2;

SELECT DISTINCT activity FROM api.activities;

SELECT DISTINCT activity_type FROM api.activities;


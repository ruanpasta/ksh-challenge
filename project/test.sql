SELECT * FROM api.activities limit 2;

SELECT COUNT(*) FROM api.activities;

SELECT MAX(planned_amount) AS max_planned_amount FROM api.activities;

SELECT MAX(executed_amount) AS max_executed_amount FROM api.activities;

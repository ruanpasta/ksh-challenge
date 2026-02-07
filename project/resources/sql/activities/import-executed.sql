INSERT INTO api.activities (
	hash_id,
  date,
  activity,
  activity_type,
  unit,
  planned_amount,
  executed_amount
)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (hash_id)
DO UPDATE
SET executed_amount = EXCLUDED.executed_amount;

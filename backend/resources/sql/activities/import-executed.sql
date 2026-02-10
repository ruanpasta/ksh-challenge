INSERT INTO api.activities (
	hash_id,
  created_at,
  activity,
  activity_type,
  unit,
  planned_amount,
  executed_amount
)
VALUES (?, ?, ?, ?, ?, ?, ?)
ON CONFLICT (hash_id)
DO UPDATE
SET executed_amount = EXCLUDED.executed_amount,
		updated_at = NOW();

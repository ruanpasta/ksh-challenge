SELECT * FROM api.activities
WHERE (?::date IS NULL OR created_at = ?::date)
			AND (?::text IS NULL OR activity = ?::text)
			AND (?::text IS NULL OR activity_type = ?::text)
LIMIT 20;

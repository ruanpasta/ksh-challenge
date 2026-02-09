CREATE TABLE api.activities (
			 id SERIAL PRIMARY KEY,
			 hash_id TEXT UNIQUE NOT NULL,
			 date DATE NOT NULL,
			 activity TEXT NOT NULL,
			 activity_type TEXT NOT NULL,
			 unit TEXT NOT NULL,
			 planned_amount DECIMAL(25,17),
			 executed_amount DECIMAL(25,17)
);

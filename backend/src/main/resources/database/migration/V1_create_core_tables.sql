CREATE TABLE IF NOT EXISTS app_metadata {
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
};

CREATE TABLE IF NOT EXISTS rpg_systems {
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TEXT NOT NULL,
    updated_at TEXT
};

CREATE TABLE IF NOT EXISTS campaigns {
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id) 
};

CREATE INDEX IF NOT EXISTS idx_campaigns_system_id
ON campaigns(system_id);

INSERT OR IGNORE INTO app_metadata (key, value)
VALUES ("schema_version_label", "0.1.0");
CREATE TABLE IF NOT EXISTS spatial_states (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    map_asset_id TEXT,
    x REAL,
    y REAL,
    q REAL,
    r REAL,
    facing TEXT,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (session_id, entity_id) REFERENCES session_entities(session_id, entity_id),
    FOREIGN KEY (map_asset_id) REFERENCES assets(id),
    CHECK (
           (x IS NULL     AND y IS NULL     AND q IS NULL     AND r IS NULL)
        OR (x IS NOT NULL AND y IS NOT NULL AND q IS NULL     AND r IS NULL)
        OR (q IS NOT NULL AND r IS NOT NULL AND x IS NULL     AND y IS NULL)
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_spatial_states_session_entity
    ON spatial_states(session_id, entity_id);
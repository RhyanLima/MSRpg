-- MSRpg — Spatial e Cycle Detection
-- Contexto: posicionamento em mapa e proteção contra ciclos no runtime.

CREATE TABLE IF NOT EXISTS spatial_states (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    map_asset_id TEXT,
    x REAL,
    y REAL,
    q REAL,
    r REAL,
    zone_id TEXT,
    facing TEXT,
    data JSON,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (map_asset_id) REFERENCES assets(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_spatial_states_session_entity
    ON spatial_states(session_id, entity_id);

CREATE TABLE IF NOT EXISTS cycle_detection_configs (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    enabled INTEGER NOT NULL DEFAULT 1 CHECK (enabled IN (0, 1)),
    max_depth INTEGER NOT NULL DEFAULT 20,
    on_limit_reached TEXT NOT NULL DEFAULT 'ABORT_AND_WARN',
    allow_intentional_loops INTEGER NOT NULL DEFAULT 0 CHECK (allow_intentional_loops IN (0, 1)),
    data JSON,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cycle_detection_configs_system
    ON cycle_detection_configs(system_id);

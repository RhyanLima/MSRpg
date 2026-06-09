-- MSRpg — Dependency Graph
-- Contexto: registry unificado e arestas de dependência para import/export.

CREATE TABLE IF NOT EXISTS definition_registry (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    definition_type TEXT NOT NULL,
    definition_id TEXT NOT NULL,
    key TEXT NOT NULL,
    content_hash TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_definition_registry_system_type_key
    ON definition_registry(system_id, definition_type, key);

CREATE UNIQUE INDEX IF NOT EXISTS ux_definition_registry_type_definition
    ON definition_registry(definition_type, definition_id);

CREATE TABLE IF NOT EXISTS definition_dependencies (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    from_definition_type TEXT NOT NULL,
    from_definition_id TEXT NOT NULL,
    to_definition_type TEXT NOT NULL,
    to_definition_id TEXT NOT NULL,
    dependency_kind TEXT NOT NULL DEFAULT 'requires',
    created_at DATETIME NOT NULL,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE INDEX IF NOT EXISTS idx_definition_dependencies_from
    ON definition_dependencies(system_id, from_definition_type, from_definition_id);

CREATE INDEX IF NOT EXISTS idx_definition_dependencies_to
    ON definition_dependencies(system_id, to_definition_type, to_definition_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_definition_dependencies_edge
    ON definition_dependencies(from_definition_type, from_definition_id, to_definition_type, to_definition_id);

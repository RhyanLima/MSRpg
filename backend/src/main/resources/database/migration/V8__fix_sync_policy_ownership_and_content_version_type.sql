-- MSRpg — Fix: ownership de SyncPolicy e tipo de rpg_systems.content_version

-- rpg_systems.sync_policy -> default_sync_policy (recomendação do criador do sistema)
ALTER TABLE rpg_systems RENAME COLUMN sync_policy TO default_sync_policy;

-- campaigns.sync_policy: política efetiva de sincronização com as Definitions do sistema.
ALTER TABLE campaigns
    ADD COLUMN sync_policy TEXT NOT NULL DEFAULT 'apply_to_new_only';

-- Campanhas já existentes herdam a recomendação do sistema a que pertencem.
UPDATE campaigns
SET sync_policy = (
    SELECT s.default_sync_policy
    FROM rpg_systems s
    WHERE s.id = campaigns.system_id
);


-- rpg_systems.content_version: INTEGER -> TEXT. 
-- nota: SQLite não suporta ALTER COLUMN para trocar tipo; usa-se o padrão de reconstrução de tabela.

PRAGMA foreign_keys = OFF;

CREATE TABLE rpg_systems__new (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    engine_version TEXT,
    content_version TEXT NOT NULL DEFAULT '1.0.0',
    default_resolution_policy_id TEXT,
    default_sync_policy TEXT NOT NULL DEFAULT 'apply_to_new_only',
    settings JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

INSERT INTO rpg_systems__new (
    id, name, description, engine_version, content_version,
    default_resolution_policy_id, default_sync_policy, settings, created_at, updated_at
)
SELECT
    id, name, description, engine_version, CAST(content_version AS TEXT),
    default_resolution_policy_id, default_sync_policy, settings, created_at, updated_at
FROM rpg_systems;

DROP TABLE rpg_systems;
ALTER TABLE rpg_systems__new RENAME TO rpg_systems;

PRAGMA foreign_keys = ON;
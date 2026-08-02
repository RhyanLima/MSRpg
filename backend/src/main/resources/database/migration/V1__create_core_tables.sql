-- MSRpg — Core
-- Contexto: sistemas, campanhas, usuários locais e permissões locais.

CREATE TABLE IF NOT EXISTS app_metadata (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS rpg_systems (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    engine_version TEXT,
    content_version INTEGER NOT NULL DEFAULT 1,
    -- FK circular/opcional aplicada pela camada de serviço: resolution_policies é criada em V3.
    default_resolution_policy_id TEXT,
    sync_policy TEXT NOT NULL DEFAULT 'apply_to_new_only',
    settings JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS campaigns (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    -- FK circular/opcional aplicada pela camada de serviço: sessions é criada em V7.
    current_session_id TEXT,
    snapshot_policy JSON,
    settings JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE INDEX IF NOT EXISTS idx_campaigns_system_id
ON campaigns(system_id);

CREATE TABLE IF NOT EXISTS local_users (
    id TEXT PRIMARY KEY,
    display_name TEXT NOT NULL,
    kind TEXT NOT NULL DEFAULT 'local',
    -- FK circular/opcional aplicada pela camada de serviço: assets é criada em V5.
    avatar_asset_id TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS role_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    permissions JSON NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_role_definitions_system_key
    ON role_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS campaign_members (
    id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    role_definition_id TEXT,
    member_type TEXT NOT NULL DEFAULT 'player',
    permissions_override JSON,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (user_id) REFERENCES local_users(id),
    FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_campaign_members_campaign_user
    ON campaign_members(campaign_id, user_id);

INSERT OR IGNORE INTO app_metadata (key, value)
VALUES ("schema_version_label", "0.1.0");

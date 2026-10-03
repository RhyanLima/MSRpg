CREATE TABLE IF NOT EXISTS app_metadata (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS rpg_systems (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    engine_version TEXT NOT NULL
        CHECK (engine_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND engine_version NOT GLOB '*[^0-9.]*'
               AND length(engine_version) - length(replace(engine_version, '.', '')) = 2),
    content_version TEXT NOT NULL DEFAULT '1.0.0'
        CHECK (content_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND content_version NOT GLOB '*[^0-9.]*'
               AND length(content_version) - length(replace(content_version, '.', '')) = 2),
    default_resolution_policy_id TEXT NOT NULL,
    default_snapshot_policy_id TEXT NOT NULL,
    default_sync_policy TEXT NOT NULL DEFAULT 'APPLY_TO_NEW_ONLY'
        CHECK (default_sync_policy IN ('APPLY_TO_NEW_ONLY', 'APPLY_TO_NEXT_CAMPAIGN', 'APPLY_TO_NEXT_SESSION')),
    missing_component_policy TEXT NOT NULL DEFAULT 'WARN_AND_SKIP_STEP'
        CHECK (missing_component_policy IN ('WARN_AND_SKIP_STEP', 'FAIL_EVENT', 'IGNORE_SILENTLY')),
    conflict_resolution_strategy TEXT NOT NULL DEFAULT 'ASK_USER'
        CHECK (conflict_resolution_strategy IN ('ASK_USER', 'SKIP', 'OVERWRITE', 'CREATE_COPY')),
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (id, default_resolution_policy_id)
        REFERENCES resolution_policies(system_id, id) DEFERRABLE INITIALLY DEFERRED,
    FOREIGN KEY (id, default_snapshot_policy_id)
        REFERENCES snapshot_policies(system_id, id) DEFERRABLE INITIALLY DEFERRED
);

CREATE TABLE IF NOT EXISTS campaigns (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    current_session_id TEXT,
    snapshot_policy_binding TEXT NOT NULL DEFAULT 'INHERIT'
        CHECK (snapshot_policy_binding IN ('INHERIT', 'PINNED')),
    snapshot_policy_id TEXT,
    sync_policy_binding TEXT NOT NULL DEFAULT 'INHERIT'
        CHECK (sync_policy_binding IN ('INHERIT', 'PINNED')),
    sync_policy TEXT
        CHECK (sync_policy IN ('APPLY_TO_NEW_ONLY', 'APPLY_TO_NEXT_CAMPAIGN', 'APPLY_TO_NEXT_SESSION')),
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (system_id, snapshot_policy_id) REFERENCES snapshot_policies(system_id, id),
    FOREIGN KEY (id, current_session_id)
        REFERENCES sessions(campaign_id, id) DEFERRABLE INITIALLY DEFERRED,
    CHECK ((snapshot_policy_binding = 'PINNED') = (snapshot_policy_id IS NOT NULL)),
    CHECK ((sync_policy_binding = 'PINNED') = (sync_policy IS NOT NULL))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_campaigns_system_id
    ON campaigns(system_id, id);

CREATE TABLE IF NOT EXISTS local_users (
    id TEXT PRIMARY KEY,
    display_name TEXT NOT NULL,
    kind TEXT NOT NULL DEFAULT 'LOCAL' CHECK (kind IN ('LOCAL', 'GUEST')),
    avatar_asset_id TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (avatar_asset_id) REFERENCES assets(id)          -- assets é criada em V4
);

CREATE TABLE IF NOT EXISTS role_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_role_definitions_system_key
    ON role_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_definition_id TEXT NOT NULL,
    permission TEXT NOT NULL CHECK (permission IN (
        'SYSTEM_EDIT', 'CAMPAIGN_EDIT', 'SESSION_START', 'SESSION_CONTROL',
        'SNAPSHOT_CREATE', 'SNAPSHOT_ROLLBACK', 'ENTITY_VIEW_ALL', 'ENTITY_EDIT_OWN',
        'ENTITY_EDIT_ALL', 'ROLL_DICE', 'LORE_VIEW', 'LORE_EDIT')),
    PRIMARY KEY (role_definition_id, permission),
    FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id) ON DELETE CASCADE
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS campaign_members (
    id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    member_type TEXT NOT NULL DEFAULT 'PLAYER' CHECK (member_type IN ('MASTER', 'PLAYER', 'SPECTATOR')),
    created_at TEXT NOT NULL,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (user_id) REFERENCES local_users(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_campaign_members_campaign_user
    ON campaign_members(campaign_id, user_id);

CREATE TABLE IF NOT EXISTS campaign_member_permission_overrides (
    campaign_member_id TEXT NOT NULL,
    permission TEXT NOT NULL CHECK (permission IN (
        'SYSTEM_EDIT', 'CAMPAIGN_EDIT', 'SESSION_START', 'SESSION_CONTROL',
        'SNAPSHOT_CREATE', 'SNAPSHOT_ROLLBACK', 'ENTITY_VIEW_ALL', 'ENTITY_EDIT_OWN',
        'ENTITY_EDIT_ALL', 'ROLL_DICE', 'LORE_VIEW', 'LORE_EDIT')),
    effect TEXT NOT NULL CHECK (effect IN ('GRANT', 'DENY')),
    PRIMARY KEY (campaign_member_id, permission),
    FOREIGN KEY (campaign_member_id) REFERENCES campaign_members(id) ON DELETE CASCADE
) WITHOUT ROWID;

INSERT OR IGNORE INTO app_metadata (key, value)
VALUES ('schema_version_label', '0.1.0');
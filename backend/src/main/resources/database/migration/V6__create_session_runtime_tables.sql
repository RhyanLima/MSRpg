CREATE TABLE IF NOT EXISTS sessions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    resolution_policy_id TEXT,
    name TEXT,
    status TEXT NOT NULL DEFAULT 'CREATED'
        CHECK (status IN ('CREATED', 'ACTIVE', 'PAUSED', 'ENDED', 'INTERRUPTED')),
    current_turn INTEGER NOT NULL DEFAULT 0 CHECK (current_turn >= 0),
    current_phase TEXT,
    runtime_settings JSON,                      -- snapshot congelado das configurações efetivas
    engine_version TEXT NOT NULL
        CHECK (engine_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND engine_version NOT GLOB '*[^0-9.]*'
               AND length(engine_version) - length(replace(engine_version, '.', '')) = 2),
    system_content_version TEXT NOT NULL
        CHECK (system_content_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND system_content_version NOT GLOB '*[^0-9.]*'
               AND length(system_content_version) - length(replace(system_content_version, '.', '')) = 2),
    started_at TEXT,
    ended_at TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id, campaign_id) REFERENCES campaigns(system_id, id),
    FOREIGN KEY (system_id, resolution_policy_id) REFERENCES resolution_policies(system_id, id),
    UNIQUE (campaign_id, id),
    CHECK (status = 'CREATED' OR resolution_policy_id IS NOT NULL),
    CHECK (ended_at IS NULL OR status = 'ENDED')
);

CREATE INDEX IF NOT EXISTS idx_sessions_campaign_status
    ON sessions(campaign_id, status);

CREATE TABLE IF NOT EXISTS session_participants (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    role_definition_id TEXT,
    connection_status TEXT NOT NULL DEFAULT 'OFFLINE'
        CHECK (connection_status IN ('OFFLINE', 'CONNECTING', 'ONLINE')),
    joined_at TEXT,
    left_at TEXT,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (user_id) REFERENCES local_users(id),
    FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_session_participants_session_user
    ON session_participants(session_id, user_id);

CREATE TABLE IF NOT EXISTS session_entities (
    id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1)),
    joined_turn INTEGER CHECK (joined_turn IS NULL OR joined_turn >= 0),
    left_turn INTEGER,
    FOREIGN KEY (campaign_id, session_id) REFERENCES sessions(campaign_id, id),
    FOREIGN KEY (campaign_id, entity_id) REFERENCES entity_instances(campaign_id, id),
    CHECK (left_turn IS NULL OR joined_turn IS NULL OR left_turn >= joined_turn)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_session_entities_session_entity
    ON session_entities(session_id, entity_id);

CREATE TABLE IF NOT EXISTS entity_runtime_states (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    attributes JSON NOT NULL,
    temporary_modifiers JSON,
    cooldowns JSON,
    data JSON,
    updated_at TEXT NOT NULL,
    FOREIGN KEY (session_id, entity_id) REFERENCES session_entities(session_id, entity_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_runtime_states_session_entity
    ON entity_runtime_states(session_id, entity_id);

CREATE TABLE IF NOT EXISTS active_states (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    effect_definition_id TEXT NOT NULL,
    source_entity_id TEXT,
    applied_by_action_id TEXT,
    activation_event_key TEXT NOT NULL,
    activation_expr TEXT NOT NULL,
    expiration_event_key TEXT,
    expiration_expr TEXT,
    remaining_turns INTEGER CHECK (remaining_turns IS NULL OR remaining_turns >= 0),
    remaining_sessions INTEGER CHECK (remaining_sessions IS NULL OR remaining_sessions >= 0),
    stacks INTEGER NOT NULL DEFAULT 1 CHECK (stacks >= 1),
    state_data JSON,
    applied_at_turn INTEGER CHECK (applied_at_turn IS NULL OR applied_at_turn >= 0),
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (session_id, entity_id) REFERENCES session_entities(session_id, entity_id),
    FOREIGN KEY (effect_definition_id) REFERENCES effect_definitions(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (applied_by_action_id) REFERENCES action_history(id),
    CHECK ((expiration_event_key IS NULL) = (expiration_expr IS NULL))
);

CREATE INDEX IF NOT EXISTS idx_active_states_session_entity
    ON active_states(session_id, entity_id);

CREATE INDEX IF NOT EXISTS idx_active_states_session_activation_event
    ON active_states(session_id, activation_event_key);

CREATE INDEX IF NOT EXISTS idx_active_states_session_expiration_event
    ON active_states(session_id, expiration_event_key) WHERE expiration_event_key IS NOT NULL;

CREATE TABLE IF NOT EXISTS event_queue_entries (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    queue_order INTEGER NOT NULL,
    event_definition_id TEXT,
    event_key TEXT NOT NULL,
    source_type TEXT NOT NULL CHECK (source_type IN ('ACTION', 'STATE', 'EVENT', 'SYSTEM')),
    source_id TEXT,
    source_entity_id TEXT,
    target_entity_id TEXT,
    params JSON,
    depth INTEGER NOT NULL DEFAULT 0 CHECK (depth >= 0),
    status TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'PROCESSING', 'PROCESSED', 'FAILED', 'ABORTED')),
    created_at TEXT NOT NULL,
    processed_at TEXT,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (event_definition_id) REFERENCES event_definitions(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (target_entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_event_queue_entries_session_order
    ON event_queue_entries(session_id, queue_order);

CREATE INDEX IF NOT EXISTS idx_event_queue_entries_session_status_order
    ON event_queue_entries(session_id, status, queue_order);

CREATE TABLE IF NOT EXISTS modifier_batches (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    event_queue_entry_id TEXT NOT NULL,
    event_key TEXT NOT NULL,
    modifier_commit_strategy TEXT NOT NULL DEFAULT 'BATCHED'
        CHECK (modifier_commit_strategy IN ('BATCHED', 'IMMEDIATE')),
    status TEXT NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMMITTED', 'DISCARDED')),
    state_before JSON,
    state_after JSON,
    created_at TEXT NOT NULL,
    committed_at TEXT,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (event_queue_entry_id) REFERENCES event_queue_entries(id),
    CHECK ((status = 'COMMITTED') = (committed_at IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS idx_modifier_batches_session
    ON modifier_batches(session_id);

CREATE INDEX IF NOT EXISTS idx_modifier_batches_event_queue_entry
    ON modifier_batches(event_queue_entry_id);

CREATE TABLE IF NOT EXISTS runtime_modifiers (
    id TEXT PRIMARY KEY,
    batch_id TEXT NOT NULL,
    target_entity_id TEXT NOT NULL,
    target_path TEXT NOT NULL,
    operation TEXT NOT NULL CHECK (operation IN ('ADD', 'SUB')),
    expression TEXT NOT NULL,
    resolved_expression TEXT,
    layer TEXT NOT NULL CHECK (layer IN ('BASE', 'ADDITIVE', 'MULTIPLICATIVE', 'OVERRIDE')),
    priority INTEGER NOT NULL DEFAULT 0,
    result_value REAL,
    source_type TEXT CHECK (source_type IN ('ACTION', 'STATE', 'ITEM', 'CATEGORY', 'EVENT', 'SYSTEM')),
    source_id TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES modifier_batches(id) ON DELETE CASCADE,
    FOREIGN KEY (target_entity_id) REFERENCES entity_instances(id)
);

CREATE INDEX IF NOT EXISTS idx_runtime_modifiers_batch
    ON runtime_modifiers(batch_id);

CREATE TABLE IF NOT EXISTS turn_order_entries (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    order_index INTEGER NOT NULL,
    initiative_value REAL,
    is_current INTEGER NOT NULL DEFAULT 0 CHECK (is_current IN (0, 1)),
    FOREIGN KEY (session_id, entity_id) REFERENCES session_entities(session_id, entity_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_turn_order_entries_session_order
    ON turn_order_entries(session_id, order_index);

CREATE UNIQUE INDEX IF NOT EXISTS ux_turn_order_entries_session_entity
    ON turn_order_entries(session_id, entity_id);

CREATE UNIQUE INDEX IF NOT EXISTS ux_turn_order_entries_one_current
    ON turn_order_entries(session_id) WHERE is_current = 1;

CREATE TABLE IF NOT EXISTS action_history (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT,
    turn INTEGER NOT NULL CHECK (turn >= 0),
    phase TEXT,
    action_key TEXT NOT NULL,
    params JSON,
    result JSON,
    created_at TEXT NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE INDEX IF NOT EXISTS idx_action_history_session_turn
    ON action_history(session_id, turn);

CREATE INDEX IF NOT EXISTS idx_action_history_session_entity_turn
    ON action_history(session_id, entity_id, turn);

CREATE TABLE IF NOT EXISTS pending_roll_requests (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT,
    request_id TEXT NOT NULL,
    dice_expr TEXT NOT NULL,
    context TEXT,
    status TEXT NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'RESOLVED', 'EXPIRED', 'CANCELLED')),
    resolved_expr TEXT,
    rolls JSON,
    total REAL,
    created_at TEXT NOT NULL,
    resolved_at TEXT,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    CHECK (status <> 'RESOLVED' OR (resolved_at IS NOT NULL AND total IS NOT NULL))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pending_roll_requests_session_request
    ON pending_roll_requests(session_id, request_id);

CREATE TABLE IF NOT EXISTS session_logs (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    schema_version INTEGER NOT NULL CHECK (schema_version >= 1),
    sequence_number INTEGER NOT NULL CHECK (sequence_number >= 1),
    timestamp TEXT NOT NULL,
    turn INTEGER,
    phase TEXT,
    level TEXT NOT NULL DEFAULT 'INFO' CHECK (level IN ('DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL')),
    source_type TEXT CHECK (source_type IN ('ACTION', 'STATE', 'EVENT', 'SYSTEM')),
    source_key TEXT,
    source_entity_id TEXT,
    event_key TEXT,
    target_entity_id TEXT,
    params JSON,
    rolls JSON,
    modifiers JSON,
    state_before JSON,
    state_after JSON,
    warnings JSON,
    message TEXT,
    data JSON,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (target_entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_session_logs_session_sequence
    ON session_logs(session_id, sequence_number);

CREATE INDEX IF NOT EXISTS idx_session_logs_session_turn
    ON session_logs(session_id, turn);

CREATE INDEX IF NOT EXISTS idx_session_logs_session_level
    ON session_logs(session_id, level);

CREATE TRIGGER IF NOT EXISTS trg_session_logs_no_update
BEFORE UPDATE ON session_logs
BEGIN
    SELECT RAISE(ABORT, 'session_logs is append-only');
END;

CREATE TRIGGER IF NOT EXISTS trg_session_logs_delete_only_ended
BEFORE DELETE ON session_logs
WHEN (SELECT status FROM sessions WHERE id = OLD.session_id) <> 'ENDED'
BEGIN
    SELECT RAISE(ABORT, 'session logs can only be purged after the session has ended');
END;

CREATE TABLE IF NOT EXISTS session_snapshots (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    snapshot_type TEXT NOT NULL DEFAULT 'MANUAL'
        CHECK (snapshot_type IN ('MANUAL', 'AUTOMATIC', 'PRE_ROLLBACK')),
    schema_version INTEGER NOT NULL CHECK (schema_version >= 1),
    engine_version TEXT NOT NULL
        CHECK (engine_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND engine_version NOT GLOB '*[^0-9.]*'
               AND length(engine_version) - length(replace(engine_version, '.', '')) = 2),
    turn INTEGER,
    phase TEXT,
    full_state JSON NOT NULL,
    log_sequence_number INTEGER,
    created_by_user_id TEXT,
    created_at TEXT NOT NULL,
    note TEXT,
    FOREIGN KEY (campaign_id, session_id) REFERENCES sessions(campaign_id, id),
    FOREIGN KEY (created_by_user_id) REFERENCES local_users(id)
);

CREATE INDEX IF NOT EXISTS idx_session_snapshots_session_created_at
    ON session_snapshots(session_id, created_at);

CREATE TRIGGER IF NOT EXISTS trg_session_snapshots_no_update
BEFORE UPDATE ON session_snapshots
BEGIN
    SELECT RAISE(ABORT, 'session_snapshots are immutable');
END;
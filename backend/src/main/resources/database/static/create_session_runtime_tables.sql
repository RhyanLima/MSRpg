-- MSRpg — Session Runtime Layer
-- Contexto: sessão ativa, estado runtime, fila de eventos, modificadores, logs e snapshots.

CREATE TABLE IF NOT EXISTS sessions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    resolution_policy_id TEXT,
    name TEXT,
    status TEXT NOT NULL DEFAULT 'created',
    current_turn INTEGER NOT NULL DEFAULT 0,
    current_phase TEXT,
    runtime_settings JSON,
    started_at DATETIME,
    ended_at DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (resolution_policy_id) REFERENCES resolution_policies(id)
);

CREATE INDEX IF NOT EXISTS idx_sessions_campaign_status
    ON sessions(campaign_id, status);

CREATE TABLE IF NOT EXISTS session_participants (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    role_definition_id TEXT,
    connection_status TEXT NOT NULL DEFAULT 'offline',
    joined_at DATETIME,
    left_at DATETIME,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (user_id) REFERENCES local_users(id),
    FOREIGN KEY (role_definition_id) REFERENCES role_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_session_participants_session_user
    ON session_participants(session_id, user_id);

CREATE TABLE IF NOT EXISTS session_entities (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    is_active INTEGER NOT NULL DEFAULT 1 CHECK (is_active IN (0, 1)),
    joined_turn INTEGER,
    left_turn INTEGER,
    data JSON,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
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
    actions_used JSON,
    data JSON,
    updated_at DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_runtime_states_session_entity
    ON entity_runtime_states(session_id, entity_id);

CREATE TABLE IF NOT EXISTS active_states (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    effect_definition_id TEXT NOT NULL,
    source_entity_id TEXT,
    activation_condition JSON,
    expiration_condition JSON,
    listener_event_key TEXT,
    remaining_turns INTEGER,
    remaining_sessions INTEGER,
    stacks INTEGER NOT NULL DEFAULT 1,
    state_data JSON,
    applied_at_turn INTEGER,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (effect_definition_id) REFERENCES effect_definitions(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id)
);

CREATE INDEX IF NOT EXISTS idx_active_states_session_entity
    ON active_states(session_id, entity_id);

CREATE INDEX IF NOT EXISTS idx_active_states_session_listener_event
    ON active_states(session_id, listener_event_key);

CREATE TABLE IF NOT EXISTS active_listeners (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    active_state_id TEXT,
    listener_event_key TEXT NOT NULL,
    listener_type TEXT NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    data JSON,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (active_state_id) REFERENCES active_states(id)
);

CREATE INDEX IF NOT EXISTS idx_active_listeners_session_listener_event
    ON active_listeners(session_id, listener_event_key);

CREATE TABLE IF NOT EXISTS event_queue_entries (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    queue_order INTEGER NOT NULL,
    event_definition_id TEXT,
    event_key TEXT NOT NULL,
    source_type TEXT NOT NULL,
    source_id TEXT,
    source_entity_id TEXT,
    target_entity_id TEXT,
    params JSON,
    depth INTEGER NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'pending',
    created_at DATETIME NOT NULL,
    processed_at DATETIME,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (event_definition_id) REFERENCES event_definitions(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (target_entity_id) REFERENCES entity_instances(id)
);

CREATE INDEX IF NOT EXISTS idx_event_queue_entries_session_status_order
    ON event_queue_entries(session_id, status, queue_order);

CREATE TABLE IF NOT EXISTS modifier_batches (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    event_queue_entry_id TEXT NOT NULL,
    event_key TEXT NOT NULL,
    commit_strategy TEXT NOT NULL DEFAULT 'BATCHED',
    status TEXT NOT NULL DEFAULT 'pending',
    state_before JSON,
    state_after JSON,
    created_at DATETIME NOT NULL,
    committed_at DATETIME,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (event_queue_entry_id) REFERENCES event_queue_entries(id)
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
    operation TEXT NOT NULL,
    expression TEXT NOT NULL,
    resolved_expression TEXT,
    layer TEXT NOT NULL,
    priority INTEGER NOT NULL DEFAULT 0,
    result_value JSON,
    source_type TEXT,
    source_id TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (batch_id) REFERENCES modifier_batches(id),
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
    data JSON,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_turn_order_entries_session_order
    ON turn_order_entries(session_id, order_index);

CREATE UNIQUE INDEX IF NOT EXISTS ux_turn_order_entries_session_entity
    ON turn_order_entries(session_id, entity_id);

CREATE TABLE IF NOT EXISTS action_history (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    entity_id TEXT,
    turn INTEGER NOT NULL,
    phase TEXT,
    action_key TEXT NOT NULL,
    params JSON,
    result JSON,
    created_at DATETIME NOT NULL,
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
    status TEXT NOT NULL DEFAULT 'pending',
    resolved_expr TEXT,
    rolls JSON,
    total REAL,
    created_at DATETIME NOT NULL,
    resolved_at DATETIME,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pending_roll_requests_session_request
    ON pending_roll_requests(session_id, request_id);

CREATE TABLE IF NOT EXISTS session_logs (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    sequence_number INTEGER NOT NULL,
    timestamp DATETIME NOT NULL,
    turn INTEGER,
    phase TEXT,
    level TEXT NOT NULL DEFAULT 'INFO',
    source_type TEXT,
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

CREATE TABLE IF NOT EXISTS session_snapshots (
    id TEXT PRIMARY KEY,
    session_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    snapshot_type TEXT NOT NULL DEFAULT 'manual',
    turn INTEGER,
    phase TEXT,
    full_state JSON NOT NULL,
    log_sequence_number INTEGER,
    created_by_user_id TEXT,
    created_at DATETIME NOT NULL,
    note TEXT,
    FOREIGN KEY (session_id) REFERENCES sessions(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (created_by_user_id) REFERENCES local_users(id)
);

CREATE INDEX IF NOT EXISTS idx_session_snapshots_session_created_at
    ON session_snapshots(session_id, created_at);

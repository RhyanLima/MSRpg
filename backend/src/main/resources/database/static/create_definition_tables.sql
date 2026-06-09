-- MSRpg — Definition Layer
-- Contexto: catálogo do sistema, regras, eventos, pipelines, skills, efeitos e itens.

CREATE TABLE IF NOT EXISTS attribute_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    value_type TEXT NOT NULL,
    default_value JSON,
    min_value JSON,
    max_value JSON,
    tags JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_attribute_definitions_system_key
    ON attribute_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS component_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    schema JSON NOT NULL,
    default_data JSON,
    is_core INTEGER NOT NULL DEFAULT 0 CHECK (is_core IN (0, 1)),
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_component_definitions_system_key
    ON component_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS component_requirements (
    id TEXT PRIMARY KEY,
    component_definition_id TEXT NOT NULL,
    required_component_id TEXT NOT NULL,
    FOREIGN KEY (component_definition_id) REFERENCES component_definitions(id),
    FOREIGN KEY (required_component_id) REFERENCES component_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_component_requirements_component_required
    ON component_requirements(component_definition_id, required_component_id);

CREATE TABLE IF NOT EXISTS entity_templates (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    semantic_type TEXT NOT NULL,
    description TEXT,
    component_ids JSON NOT NULL,
    category_ids JSON,
    base_attributes JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_templates_system_key
    ON entity_templates(system_id, key);

CREATE INDEX IF NOT EXISTS idx_entity_templates_system_semantic_type
    ON entity_templates(system_id, semantic_type);

CREATE TABLE IF NOT EXISTS category_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    category_type TEXT,
    description TEXT,
    grants JSON NOT NULL,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_category_definitions_system_key
    ON category_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_category_definitions_system_category_type
    ON category_definitions(system_id, category_type);

CREATE TABLE IF NOT EXISTS dice_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    sides INTEGER,
    expression TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_dice_definitions_system_key
    ON dice_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS pipeline_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_definitions_system_key
    ON pipeline_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS action_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    params_schema JSON,
    pipeline_definition_id TEXT,
    required_components JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (pipeline_definition_id) REFERENCES pipeline_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_action_definitions_system_key
    ON action_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS event_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    params_schema JSON,
    emits JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_event_definitions_system_key
    ON event_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS modifier_definitions (
    id TEXT PRIMARY KEY,
    event_definition_id TEXT NOT NULL,
    target_path TEXT NOT NULL,
    operation TEXT NOT NULL,
    expression TEXT NOT NULL,
    layer TEXT NOT NULL DEFAULT 'ADDITIVE',
    priority INTEGER NOT NULL DEFAULT 0,
    condition_expr TEXT,
    data JSON,
    FOREIGN KEY (event_definition_id) REFERENCES event_definitions(id)
);

CREATE INDEX IF NOT EXISTS idx_modifier_definitions_event_definition
    ON modifier_definitions(event_definition_id);

CREATE TABLE IF NOT EXISTS pipeline_steps (
    id TEXT PRIMARY KEY,
    pipeline_definition_id TEXT NOT NULL,
    step_order INTEGER NOT NULL,
    event_definition_id TEXT NOT NULL,
    params_expr JSON,
    condition_expr TEXT,
    FOREIGN KEY (pipeline_definition_id) REFERENCES pipeline_definitions(id),
    FOREIGN KEY (event_definition_id) REFERENCES event_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_steps_pipeline_order
    ON pipeline_steps(pipeline_definition_id, step_order);

CREATE TABLE IF NOT EXISTS resolution_policies (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    default_order JSON NOT NULL,
    overrides JSON,
    modifier_layer_order JSON,
    commit_strategy TEXT NOT NULL DEFAULT 'BATCHED',
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_resolution_policies_system_key
    ON resolution_policies(system_id, key);

CREATE TABLE IF NOT EXISTS rule_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    listens_to_event_key TEXT,
    resolution_step TEXT,
    priority INTEGER NOT NULL DEFAULT 0,
    condition_expr TEXT,
    graph JSON NOT NULL,
    emits JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_rule_definitions_system_key
    ON rule_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_rule_definitions_system_listens_to_event
    ON rule_definitions(system_id, listens_to_event_key);

CREATE TABLE IF NOT EXISTS skill_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    cost_expr JSON,
    params_schema JSON,
    pipeline_definition_id TEXT,
    cooldown_policy JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (pipeline_definition_id) REFERENCES pipeline_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_skill_definitions_system_key
    ON skill_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS effect_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    default_duration JSON,
    activation_condition JSON,
    expiration_condition JSON,
    stack_policy TEXT NOT NULL DEFAULT 'REFRESH',
    modifiers JSON,
    pipeline_definition_id TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (pipeline_definition_id) REFERENCES pipeline_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_effect_definitions_system_key
    ON effect_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS item_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    item_type TEXT,
    description TEXT,
    component_ids JSON,
    base_properties JSON,
    granted_modifiers JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_definitions_system_key
    ON item_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_item_definitions_system_item_type
    ON item_definitions(system_id, item_type);

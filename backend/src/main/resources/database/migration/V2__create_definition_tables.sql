CREATE TABLE IF NOT EXISTS attribute_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    value_type TEXT NOT NULL CHECK (value_type IN ('NUMBER', 'INTEGER', 'BOOLEAN', 'TEXT')),
    default_value JSON,
    min_value JSON,
    max_value JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_attribute_definitions_system_key
    ON attribute_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS attribute_tags (
    attribute_definition_id TEXT NOT NULL,
    tag TEXT NOT NULL CHECK (tag = lower(tag) AND length(tag) BETWEEN 1 AND 32),
    PRIMARY KEY (attribute_definition_id, tag),
    FOREIGN KEY (attribute_definition_id) REFERENCES attribute_definitions(id) ON DELETE CASCADE
) WITHOUT ROWID;

CREATE INDEX IF NOT EXISTS idx_attribute_tags_tag
    ON attribute_tags(tag);

CREATE TABLE IF NOT EXISTS component_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    schema JSON NOT NULL,                       
    default_data JSON,
    is_core INTEGER NOT NULL DEFAULT 0 CHECK (is_core IN (0, 1)),
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_component_definitions_system_key
    ON component_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS component_requirements (
    system_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    required_component_id TEXT NOT NULL,
    PRIMARY KEY (component_definition_id, required_component_id),
    FOREIGN KEY (system_id, component_definition_id)
        REFERENCES component_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, required_component_id)
        REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT,
    CHECK (component_definition_id <> required_component_id)
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS entity_templates (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    semantic_type TEXT NOT NULL,
    description TEXT,
    base_attributes JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_templates_system_key
    ON entity_templates(system_id, key);

CREATE INDEX IF NOT EXISTS idx_entity_templates_system_semantic_type
    ON entity_templates(system_id, semantic_type);

CREATE TABLE IF NOT EXISTS entity_template_components (
    system_id TEXT NOT NULL,
    template_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    ordinal INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (template_id, component_definition_id),
    FOREIGN KEY (system_id, template_id)
        REFERENCES entity_templates(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, component_definition_id)
        REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE INDEX IF NOT EXISTS idx_entity_template_components_component
    ON entity_template_components(component_definition_id);

CREATE TABLE IF NOT EXISTS category_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    category_type TEXT,
    description TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_category_definitions_system_key
    ON category_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_category_definitions_system_category_type
    ON category_definitions(system_id, category_type);

CREATE TABLE IF NOT EXISTS entity_template_categories (
    system_id TEXT NOT NULL,
    template_id TEXT NOT NULL,
    category_definition_id TEXT NOT NULL,
    ordinal INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (template_id, category_definition_id),
    FOREIGN KEY (system_id, template_id)
        REFERENCES entity_templates(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, category_definition_id)
        REFERENCES category_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE INDEX IF NOT EXISTS idx_entity_template_categories_category
    ON entity_template_categories(category_definition_id);

CREATE TABLE IF NOT EXISTS dice_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    sides INTEGER CHECK (sides IS NULL OR sides >= 1),
    expression TEXT,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id),
    CHECK (sides IS NOT NULL OR expression IS NOT NULL)
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
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_definitions_system_key
    ON pipeline_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS event_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL
        CHECK (key GLOB '[A-Z]*' AND key NOT GLOB '*[^A-Z0-9_]*' AND length(key) <= 64),
    name TEXT NOT NULL,
    description TEXT,
    params_schema JSON,
    is_core INTEGER NOT NULL DEFAULT 0 CHECK (is_core IN (0, 1)),
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_event_definitions_system_key
    ON event_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS event_emissions (
    system_id TEXT NOT NULL,
    event_definition_id TEXT NOT NULL,
    emitted_event_definition_id TEXT NOT NULL,
    condition_expr TEXT NOT NULL DEFAULT 'true',
    ordinal INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (event_definition_id, emitted_event_definition_id),
    FOREIGN KEY (system_id, event_definition_id)
        REFERENCES event_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, emitted_event_definition_id)
        REFERENCES event_definitions(system_id, id) ON DELETE RESTRICT,
    CHECK (event_definition_id <> emitted_event_definition_id)
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS pipeline_steps (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    pipeline_definition_id TEXT NOT NULL,
    step_order INTEGER NOT NULL CHECK (step_order >= 0),
    event_definition_id TEXT NOT NULL,
    params_expr JSON,
    condition_expr TEXT NOT NULL DEFAULT 'true',
    FOREIGN KEY (system_id, pipeline_definition_id)
        REFERENCES pipeline_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, event_definition_id)
        REFERENCES event_definitions(system_id, id) ON DELETE RESTRICT
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_pipeline_steps_pipeline_order
    ON pipeline_steps(pipeline_definition_id, step_order);

CREATE TABLE IF NOT EXISTS action_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    params_schema JSON,
    pipeline_definition_id TEXT,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (system_id, pipeline_definition_id) REFERENCES pipeline_definitions(system_id, id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_action_definitions_system_key
    ON action_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS action_required_components (
    system_id TEXT NOT NULL,
    action_definition_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    PRIMARY KEY (action_definition_id, component_definition_id),
    FOREIGN KEY (system_id, action_definition_id)
        REFERENCES action_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, component_definition_id)
        REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS resolution_policies (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    default_order JSON NOT NULL,
    overrides JSON,
    modifier_layer_order JSON,
    modifier_commit_strategy TEXT NOT NULL DEFAULT 'BATCHED'
        CHECK (modifier_commit_strategy IN ('BATCHED', 'IMMEDIATE')),
    cycle_detection_enabled INTEGER NOT NULL DEFAULT 1 CHECK (cycle_detection_enabled IN (0, 1)),
    cycle_max_depth INTEGER NOT NULL DEFAULT 20 CHECK (cycle_max_depth >= 1),
    cycle_on_limit_reached TEXT NOT NULL DEFAULT 'ABORT_AND_WARN'
        CHECK (cycle_on_limit_reached IN ('ABORT_AND_WARN', 'ABORT_AND_FAIL')),
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_resolution_policies_system_key
    ON resolution_policies(system_id, key);

CREATE TABLE IF NOT EXISTS snapshot_policies (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    manual_enabled INTEGER NOT NULL DEFAULT 1 CHECK (manual_enabled IN (0, 1)),
    auto_trigger TEXT NOT NULL DEFAULT 'EVERY_SESSION_END'
        CHECK (auto_trigger IN ('DISABLED', 'EVERY_TURN_END', 'EVERY_N_TURNS', 'EVERY_COMBAT_END', 'EVERY_SESSION_END')),
    auto_interval_turns INTEGER NOT NULL DEFAULT 0,
    max_to_keep INTEGER NOT NULL DEFAULT 10 CHECK (max_to_keep >= 1),
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id),
    CHECK ((auto_trigger = 'EVERY_N_TURNS') = (auto_interval_turns > 0))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_snapshot_policies_system_key
    ON snapshot_policies(system_id, key);

CREATE TABLE IF NOT EXISTS rule_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    listens_to_event_key TEXT,
    resolution_step TEXT,
    priority INTEGER NOT NULL DEFAULT 0,
    condition_expr TEXT NOT NULL DEFAULT 'true',
    graph JSON NOT NULL,                        -- grafo do editor visual: permanece JSON
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    -- renomear a key do evento propaga para a regra
    FOREIGN KEY (system_id, listens_to_event_key)
        REFERENCES event_definitions(system_id, key) ON UPDATE CASCADE,
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_rule_definitions_system_key
    ON rule_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_rule_definitions_system_listens_to_event
    ON rule_definitions(system_id, listens_to_event_key);

CREATE TABLE IF NOT EXISTS rule_emissions (
    system_id TEXT NOT NULL,
    rule_definition_id TEXT NOT NULL,
    emitted_event_definition_id TEXT NOT NULL,
    condition_expr TEXT NOT NULL DEFAULT 'true',
    ordinal INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (rule_definition_id, emitted_event_definition_id),
    FOREIGN KEY (system_id, rule_definition_id)
        REFERENCES rule_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, emitted_event_definition_id)
        REFERENCES event_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS skill_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    params_schema JSON,
    pipeline_definition_id TEXT,
    cooldown_reset_policy TEXT NOT NULL DEFAULT 'MANUAL_OR_SESSION'
        CHECK (cooldown_reset_policy IN ('COMBAT_END', 'SESSION_END', 'MANUAL_ONLY', 'MANUAL_OR_SESSION')),
    cooldown_turns INTEGER NOT NULL DEFAULT 0 CHECK (cooldown_turns >= 0),
    cooldown_sessions INTEGER NOT NULL DEFAULT 0 CHECK (cooldown_sessions >= 0),
    cooldown_expr JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (system_id, pipeline_definition_id) REFERENCES pipeline_definitions(system_id, id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_skill_definitions_system_key
    ON skill_definitions(system_id, key);

CREATE TABLE IF NOT EXISTS skill_costs (
    system_id TEXT NOT NULL,
    skill_definition_id TEXT NOT NULL,
    attribute_definition_id TEXT NOT NULL,
    amount_expr TEXT NOT NULL,
    PRIMARY KEY (skill_definition_id, attribute_definition_id),
    FOREIGN KEY (system_id, skill_definition_id)
        REFERENCES skill_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, attribute_definition_id)
        REFERENCES attribute_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS effect_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    key TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    duration_turns INTEGER NOT NULL DEFAULT 0 CHECK (duration_turns >= 0),
    duration_sessions INTEGER NOT NULL DEFAULT 0 CHECK (duration_sessions >= 0),
    stack_policy TEXT NOT NULL DEFAULT 'REFRESH'
        CHECK (stack_policy IN ('STACK', 'REFRESH', 'EXTEND', 'REPLACE', 'INDEPENDENT', 'IGNORE')),
    activation_event_key TEXT NOT NULL,
    activation_expr TEXT NOT NULL DEFAULT 'true',
    expiration_event_key TEXT,
    expiration_expr TEXT,
    pipeline_definition_id TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (system_id, activation_event_key)
        REFERENCES event_definitions(system_id, key) ON UPDATE CASCADE,
    FOREIGN KEY (system_id, expiration_event_key)
        REFERENCES event_definitions(system_id, key) ON UPDATE CASCADE,
    FOREIGN KEY (system_id, pipeline_definition_id) REFERENCES pipeline_definitions(system_id, id),
    UNIQUE (system_id, id),
    CHECK ((expiration_event_key IS NULL) = (expiration_expr IS NULL))
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
    base_properties JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    UNIQUE (system_id, id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_definitions_system_key
    ON item_definitions(system_id, key);

CREATE INDEX IF NOT EXISTS idx_item_definitions_system_item_type
    ON item_definitions(system_id, item_type);

CREATE TABLE IF NOT EXISTS item_definition_components (
    system_id TEXT NOT NULL,
    item_definition_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    PRIMARY KEY (item_definition_id, component_definition_id),
    FOREIGN KEY (system_id, item_definition_id)
        REFERENCES item_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, component_definition_id)
        REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS category_grants (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    category_definition_id TEXT NOT NULL,
    attribute_definition_id TEXT,
    skill_definition_id TEXT,
    rule_definition_id TEXT,
    component_definition_id TEXT,
    pipeline_definition_id TEXT,
    bonus REAL,
    FOREIGN KEY (system_id, category_definition_id)
        REFERENCES category_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, attribute_definition_id) REFERENCES attribute_definitions(system_id, id) ON DELETE RESTRICT,
    FOREIGN KEY (system_id, skill_definition_id)     REFERENCES skill_definitions(system_id, id)     ON DELETE RESTRICT,
    FOREIGN KEY (system_id, rule_definition_id)      REFERENCES rule_definitions(system_id, id)      ON DELETE RESTRICT,
    FOREIGN KEY (system_id, component_definition_id) REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT,
    FOREIGN KEY (system_id, pipeline_definition_id)  REFERENCES pipeline_definitions(system_id, id)  ON DELETE RESTRICT,
    CHECK ((attribute_definition_id IS NOT NULL) + (skill_definition_id IS NOT NULL)
         + (rule_definition_id IS NOT NULL) + (component_definition_id IS NOT NULL)
         + (pipeline_definition_id IS NOT NULL) = 1),
    CHECK (bonus IS NULL OR attribute_definition_id IS NOT NULL)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_category_grants_attribute ON category_grants(category_definition_id, attribute_definition_id) WHERE attribute_definition_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_category_grants_skill     ON category_grants(category_definition_id, skill_definition_id)     WHERE skill_definition_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_category_grants_rule      ON category_grants(category_definition_id, rule_definition_id)      WHERE rule_definition_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_category_grants_component ON category_grants(category_definition_id, component_definition_id) WHERE component_definition_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_category_grants_pipeline  ON category_grants(category_definition_id, pipeline_definition_id)  WHERE pipeline_definition_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS modifier_definitions (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    event_definition_id TEXT,
    effect_definition_id TEXT,
    item_definition_id TEXT,
    target_path TEXT NOT NULL,
    operation TEXT NOT NULL CHECK (operation IN ('ADD', 'SUB')),
    expression TEXT NOT NULL,
    layer TEXT NOT NULL DEFAULT 'ADDITIVE'
        CHECK (layer IN ('BASE', 'ADDITIVE', 'MULTIPLICATIVE', 'OVERRIDE')),
    priority INTEGER NOT NULL DEFAULT 0,
    condition_expr TEXT NOT NULL DEFAULT 'true',
    FOREIGN KEY (system_id, event_definition_id)  REFERENCES event_definitions(system_id, id)  ON DELETE CASCADE,
    FOREIGN KEY (system_id, effect_definition_id) REFERENCES effect_definitions(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, item_definition_id)   REFERENCES item_definitions(system_id, id)   ON DELETE CASCADE,
    CHECK ((event_definition_id IS NOT NULL) + (effect_definition_id IS NOT NULL)
         + (item_definition_id IS NOT NULL) = 1)
);

CREATE INDEX IF NOT EXISTS idx_modifier_definitions_event  ON modifier_definitions(event_definition_id)  WHERE event_definition_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_modifier_definitions_effect ON modifier_definitions(effect_definition_id) WHERE effect_definition_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_modifier_definitions_item   ON modifier_definitions(item_definition_id)   WHERE item_definition_id IS NOT NULL;

CREATE VIEW IF NOT EXISTS campaign_effective_settings AS
    SELECT c.id AS campaign_id,
           c.system_id,
           c.snapshot_policy_binding,
           CASE c.snapshot_policy_binding
               WHEN 'PINNED' THEN c.snapshot_policy_id
               ELSE s.default_snapshot_policy_id
           END AS snapshot_policy_id,
           c.sync_policy_binding,
           CASE c.sync_policy_binding
               WHEN 'PINNED' THEN c.sync_policy
               ELSE s.default_sync_policy
           END AS sync_policy
      FROM campaigns c
      JOIN rpg_systems s ON s.id = c.system_id;
-- MSRpg — Instance Layer
-- Contexto: entidades e objetos persistentes de campanha.

CREATE TABLE IF NOT EXISTS entity_instances (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    template_id TEXT,
    definition_snapshot_version INTEGER,
    semantic_type TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    base_attributes JSON,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (template_id) REFERENCES entity_templates(id)
);

CREATE INDEX IF NOT EXISTS idx_entity_instances_system
    ON entity_instances(system_id);

CREATE INDEX IF NOT EXISTS idx_entity_instances_campaign_semantic_type
    ON entity_instances(campaign_id, semantic_type);

CREATE INDEX IF NOT EXISTS idx_entity_instances_template
    ON entity_instances(template_id);

CREATE TABLE IF NOT EXISTS entity_instance_components (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    component_key TEXT NOT NULL,
    data JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (component_definition_id) REFERENCES component_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_instance_components_entity_key
    ON entity_instance_components(entity_id, component_key);

CREATE TABLE IF NOT EXISTS entity_instance_categories (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    category_definition_id TEXT NOT NULL,
    applied_snapshot_version INTEGER,
    source TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (category_definition_id) REFERENCES category_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_entity_instance_categories_entity_category
    ON entity_instance_categories(entity_id, category_definition_id);

CREATE TABLE IF NOT EXISTS item_instances (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    item_definition_id TEXT,
    as_entity_id TEXT,
    owner_entity_id TEXT,
    name TEXT,
    durability_current INTEGER,
    stack_count INTEGER NOT NULL DEFAULT 1,
    data JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (item_definition_id) REFERENCES item_definitions(id),
    FOREIGN KEY (as_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (owner_entity_id) REFERENCES entity_instances(id)
);

CREATE INDEX IF NOT EXISTS idx_item_instances_campaign_owner
    ON item_instances(campaign_id, owner_entity_id);

CREATE INDEX IF NOT EXISTS idx_item_instances_definition
    ON item_instances(item_definition_id);

CREATE TABLE IF NOT EXISTS inventory_states (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    slots INTEGER,
    weight_limit REAL,
    data JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_inventory_states_entity
    ON inventory_states(entity_id);

CREATE TABLE IF NOT EXISTS inventory_entries (
    id TEXT PRIMARY KEY,
    inventory_state_id TEXT NOT NULL,
    item_instance_id TEXT NOT NULL,
    slot_key TEXT,
    quantity INTEGER NOT NULL DEFAULT 1,
    position_index INTEGER,
    data JSON,
    FOREIGN KEY (inventory_state_id) REFERENCES inventory_states(id),
    FOREIGN KEY (item_instance_id) REFERENCES item_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_inventory_entries_inventory_item
    ON inventory_entries(inventory_state_id, item_instance_id);

CREATE TABLE IF NOT EXISTS equipment_states (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    data JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_equipment_states_entity
    ON equipment_states(entity_id);

CREATE TABLE IF NOT EXISTS equipment_slots (
    id TEXT PRIMARY KEY,
    equipment_state_id TEXT NOT NULL,
    slot_key TEXT NOT NULL,
    item_instance_id TEXT,
    data JSON,
    FOREIGN KEY (equipment_state_id) REFERENCES equipment_states(id),
    FOREIGN KEY (item_instance_id) REFERENCES item_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_equipment_slots_state_slot
    ON equipment_slots(equipment_state_id, slot_key);

CREATE TABLE IF NOT EXISTS cooldown_states (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    skill_definition_id TEXT NOT NULL,
    remaining_turns INTEGER NOT NULL DEFAULT 0,
    remaining_sessions INTEGER NOT NULL DEFAULT 0,
    reset_policy TEXT NOT NULL DEFAULT 'manual_or_session',
    data JSON,
    updated_at DATETIME,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (skill_definition_id) REFERENCES skill_definitions(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cooldown_states_entity_skill
    ON cooldown_states(entity_id, skill_definition_id);

CREATE TABLE IF NOT EXISTS relation_states (
    id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    source_entity_id TEXT NOT NULL,
    target_entity_id TEXT NOT NULL,
    relation_type TEXT NOT NULL,
    value REAL,
    data JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (source_entity_id) REFERENCES entity_instances(id),
    FOREIGN KEY (target_entity_id) REFERENCES entity_instances(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_relation_states_campaign_source_target
    ON relation_states(campaign_id, source_entity_id, target_entity_id);

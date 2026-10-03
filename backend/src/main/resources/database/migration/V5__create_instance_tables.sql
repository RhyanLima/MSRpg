CREATE TABLE IF NOT EXISTS entity_instances (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    template_id TEXT,
    definition_snapshot_version INTEGER CHECK (definition_snapshot_version IS NULL OR definition_snapshot_version >= 1),
    semantic_type TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    base_attributes JSON,
    data JSON,                                 
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id, campaign_id) REFERENCES campaigns(system_id, id),
    FOREIGN KEY (system_id, template_id) REFERENCES entity_templates(system_id, id),
    UNIQUE (system_id, id),
    UNIQUE (campaign_id, id)
);

CREATE INDEX IF NOT EXISTS idx_entity_instances_campaign_semantic_type
    ON entity_instances(campaign_id, semantic_type);

CREATE INDEX IF NOT EXISTS idx_entity_instances_template
    ON entity_instances(template_id);

CREATE TABLE IF NOT EXISTS entity_instance_components (
    system_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    component_definition_id TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    PRIMARY KEY (entity_id, component_definition_id),
    FOREIGN KEY (system_id, entity_id)
        REFERENCES entity_instances(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, component_definition_id)
        REFERENCES component_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS entity_instance_categories (
    system_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    category_definition_id TEXT NOT NULL,
    applied_snapshot_version INTEGER CHECK (applied_snapshot_version IS NULL OR applied_snapshot_version >= 1),
    source TEXT NOT NULL DEFAULT 'MANUAL' CHECK (source IN ('TEMPLATE', 'MANUAL', 'GRANTED')),
    created_at TEXT NOT NULL,
    PRIMARY KEY (entity_id, category_definition_id),
    FOREIGN KEY (system_id, entity_id)
        REFERENCES entity_instances(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, category_definition_id)
        REFERENCES category_definitions(system_id, id) ON DELETE RESTRICT
) WITHOUT ROWID;

CREATE TABLE IF NOT EXISTS item_instances (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT NOT NULL,
    item_definition_id TEXT,
    as_entity_id TEXT,
    name TEXT,
    durability_current INTEGER CHECK (durability_current IS NULL OR durability_current >= 0),
    stack_count INTEGER NOT NULL DEFAULT 1 CHECK (stack_count >= 1),
    data JSON,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id, campaign_id) REFERENCES campaigns(system_id, id),
    FOREIGN KEY (system_id, item_definition_id) REFERENCES item_definitions(system_id, id),
    FOREIGN KEY (campaign_id, as_entity_id) REFERENCES entity_instances(campaign_id, id),
    UNIQUE (campaign_id, id)
);

CREATE INDEX IF NOT EXISTS idx_item_instances_campaign
    ON item_instances(campaign_id);

CREATE INDEX IF NOT EXISTS idx_item_instances_definition
    ON item_instances(item_definition_id);

CREATE TABLE IF NOT EXISTS inventory_states (
    id TEXT PRIMARY KEY,
    entity_id TEXT NOT NULL,
    slots INTEGER CHECK (slots IS NULL OR slots >= 0),
    weight_limit REAL CHECK (weight_limit IS NULL OR weight_limit >= 0),
    data JSON,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (entity_id) REFERENCES entity_instances(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_inventory_states_entity
    ON inventory_states(entity_id);

CREATE TABLE IF NOT EXISTS item_placements (
    item_instance_id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    holder_entity_id TEXT NOT NULL,
    container TEXT NOT NULL CHECK (container IN ('INVENTORY', 'EQUIPMENT')),
    slot_key TEXT,                              -- slot de equipamento (mainHand, head...)
    position_index INTEGER CHECK (position_index IS NULL OR position_index >= 0),
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (campaign_id, item_instance_id)
        REFERENCES item_instances(campaign_id, id) ON DELETE CASCADE,
    FOREIGN KEY (campaign_id, holder_entity_id)
        REFERENCES entity_instances(campaign_id, id) ON DELETE CASCADE,
    CHECK ((container = 'EQUIPMENT') = (slot_key IS NOT NULL)),
    CHECK (container = 'INVENTORY' OR position_index IS NULL)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_placements_equipment_slot
    ON item_placements(holder_entity_id, slot_key) WHERE container = 'EQUIPMENT';

CREATE UNIQUE INDEX IF NOT EXISTS ux_item_placements_inventory_position
    ON item_placements(holder_entity_id, position_index)
    WHERE container = 'INVENTORY' AND position_index IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_item_placements_holder
    ON item_placements(holder_entity_id, container);

CREATE TABLE IF NOT EXISTS cooldown_states (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    entity_id TEXT NOT NULL,
    skill_definition_id TEXT NOT NULL,
    remaining_turns INTEGER NOT NULL DEFAULT 0 CHECK (remaining_turns >= 0),
    remaining_sessions INTEGER NOT NULL DEFAULT 0 CHECK (remaining_sessions >= 0),
    updated_at TEXT,
    FOREIGN KEY (system_id, entity_id)
        REFERENCES entity_instances(system_id, id) ON DELETE CASCADE,
    FOREIGN KEY (system_id, skill_definition_id)
        REFERENCES skill_definitions(system_id, id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cooldown_states_entity_skill
    ON cooldown_states(entity_id, skill_definition_id);

CREATE TABLE IF NOT EXISTS relation_states (
    id TEXT PRIMARY KEY,
    campaign_id TEXT NOT NULL,
    source_entity_id TEXT NOT NULL,
    target_entity_id TEXT NOT NULL,
    relation_type TEXT NOT NULL CHECK (relation_type IN ('ALLY', 'ENEMY', 'NEUTRAL', 'CUSTOM')),
    custom_label TEXT,
    value REAL,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (campaign_id, source_entity_id)
        REFERENCES entity_instances(campaign_id, id) ON DELETE CASCADE,
    FOREIGN KEY (campaign_id, target_entity_id)
        REFERENCES entity_instances(campaign_id, id) ON DELETE CASCADE,
    CHECK (source_entity_id <> target_entity_id),
    CHECK ((relation_type = 'CUSTOM') = (custom_label IS NOT NULL))
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_relation_states_campaign_source_target
    ON relation_states(campaign_id, source_entity_id, target_entity_id);
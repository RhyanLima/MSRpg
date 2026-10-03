CREATE TABLE IF NOT EXISTS definition_references (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    from_definition_type TEXT NOT NULL CHECK (from_definition_type IN (
        'ATTRIBUTE', 'COMPONENT', 'ENTITY_TEMPLATE', 'CATEGORY', 'DICE', 'PIPELINE', 'ACTION',
        'EVENT', 'RULE', 'SKILL', 'EFFECT', 'ITEM', 'RESOLUTION_POLICY', 'SNAPSHOT_POLICY')),
    from_definition_id TEXT NOT NULL,
    to_definition_type TEXT NOT NULL CHECK (to_definition_type IN (
        'ATTRIBUTE', 'COMPONENT', 'ENTITY_TEMPLATE', 'CATEGORY', 'DICE', 'PIPELINE', 'ACTION',
        'EVENT', 'RULE', 'SKILL', 'EFFECT', 'ITEM', 'RESOLUTION_POLICY', 'SNAPSHOT_POLICY')),
    to_definition_id TEXT NOT NULL,
    source_column TEXT NOT NULL,                -- ex.: 'modifier_definitions.expression'
    created_at TEXT NOT NULL,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_definition_references_edge
    ON definition_references(from_definition_type, from_definition_id, to_definition_type, to_definition_id, source_column);

CREATE INDEX IF NOT EXISTS idx_definition_references_from
    ON definition_references(system_id, from_definition_type, from_definition_id);

CREATE INDEX IF NOT EXISTS idx_definition_references_to
    ON definition_references(system_id, to_definition_type, to_definition_id);

CREATE VIEW IF NOT EXISTS definition_registry AS
              SELECT 'ATTRIBUTE' AS definition_type, id AS definition_id, system_id, key, version, content_hash FROM attribute_definitions
    UNION ALL SELECT 'COMPONENT',         id, system_id, key, version, content_hash FROM component_definitions
    UNION ALL SELECT 'ENTITY_TEMPLATE',   id, system_id, key, version, content_hash FROM entity_templates
    UNION ALL SELECT 'CATEGORY',          id, system_id, key, version, content_hash FROM category_definitions
    UNION ALL SELECT 'DICE',              id, system_id, key, version, content_hash FROM dice_definitions
    UNION ALL SELECT 'PIPELINE',          id, system_id, key, version, content_hash FROM pipeline_definitions
    UNION ALL SELECT 'ACTION',            id, system_id, key, version, content_hash FROM action_definitions
    UNION ALL SELECT 'EVENT',             id, system_id, key, version, content_hash FROM event_definitions
    UNION ALL SELECT 'RULE',              id, system_id, key, version, content_hash FROM rule_definitions
    UNION ALL SELECT 'SKILL',             id, system_id, key, version, content_hash FROM skill_definitions
    UNION ALL SELECT 'EFFECT',            id, system_id, key, version, content_hash FROM effect_definitions
    UNION ALL SELECT 'ITEM',              id, system_id, key, version, content_hash FROM item_definitions
    UNION ALL SELECT 'RESOLUTION_POLICY', id, system_id, key, version, content_hash FROM resolution_policies
    UNION ALL SELECT 'SNAPSHOT_POLICY',   id, system_id, key, version, content_hash FROM snapshot_policies;

CREATE VIEW IF NOT EXISTS definition_edges AS
              SELECT system_id, 'ENTITY_TEMPLATE' AS from_type, template_id AS from_id, 'COMPONENT' AS to_type, component_definition_id AS to_id, 'USES' AS kind FROM entity_template_components
    UNION ALL SELECT system_id, 'ENTITY_TEMPLATE', template_id, 'CATEGORY', category_definition_id, 'USES' FROM entity_template_categories
    UNION ALL SELECT system_id, 'ITEM', item_definition_id, 'COMPONENT', component_definition_id, 'USES' FROM item_definition_components
    UNION ALL SELECT system_id, 'ACTION', action_definition_id, 'COMPONENT', component_definition_id, 'REQUIRES' FROM action_required_components
    UNION ALL SELECT system_id, 'COMPONENT', component_definition_id, 'COMPONENT', required_component_id, 'REQUIRES' FROM component_requirements
    UNION ALL SELECT system_id, 'CATEGORY', category_definition_id, 'ATTRIBUTE', attribute_definition_id, 'GRANTS' FROM category_grants WHERE attribute_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'CATEGORY', category_definition_id, 'SKILL', skill_definition_id, 'GRANTS' FROM category_grants WHERE skill_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'CATEGORY', category_definition_id, 'RULE', rule_definition_id, 'GRANTS' FROM category_grants WHERE rule_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'CATEGORY', category_definition_id, 'COMPONENT', component_definition_id, 'GRANTS' FROM category_grants WHERE component_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'CATEGORY', category_definition_id, 'PIPELINE', pipeline_definition_id, 'GRANTS' FROM category_grants WHERE pipeline_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'EVENT', event_definition_id, 'EVENT', emitted_event_definition_id, 'EMITS' FROM event_emissions
    UNION ALL SELECT system_id, 'RULE', rule_definition_id, 'EVENT', emitted_event_definition_id, 'EMITS' FROM rule_emissions
    UNION ALL SELECT system_id, 'PIPELINE', pipeline_definition_id, 'EVENT', event_definition_id, 'USES' FROM pipeline_steps
    UNION ALL SELECT system_id, 'SKILL', id, 'PIPELINE', pipeline_definition_id, 'USES' FROM skill_definitions WHERE pipeline_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'ACTION', id, 'PIPELINE', pipeline_definition_id, 'USES' FROM action_definitions WHERE pipeline_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'EFFECT', id, 'PIPELINE', pipeline_definition_id, 'USES' FROM effect_definitions WHERE pipeline_definition_id IS NOT NULL
    UNION ALL SELECT system_id, 'SKILL', skill_definition_id, 'ATTRIBUTE', attribute_definition_id, 'REQUIRES' FROM skill_costs
    UNION ALL SELECT e.system_id, 'EFFECT', e.id, 'EVENT', ev.id, 'USES'
                FROM effect_definitions e JOIN event_definitions ev
                  ON ev.system_id = e.system_id AND ev.key IN (e.activation_event_key, e.expiration_event_key)
    UNION ALL SELECT r.system_id, 'RULE', r.id, 'EVENT', ev.id, 'USES'
                FROM rule_definitions r JOIN event_definitions ev
                  ON ev.system_id = r.system_id AND ev.key = r.listens_to_event_key
    UNION ALL SELECT system_id, from_definition_type, from_definition_id, to_definition_type, to_definition_id, 'REFERENCES' FROM definition_references;

CREATE VIEW IF NOT EXISTS event_flow AS
    SELECT event_definition_id AS from_event_id, emitted_event_definition_id AS to_event_id
      FROM event_emissions
    UNION
    SELECT ev.id, re.emitted_event_definition_id
      FROM rule_definitions r
      JOIN event_definitions ev ON ev.system_id = r.system_id AND ev.key = r.listens_to_event_key
      JOIN rule_emissions re ON re.rule_definition_id = r.id;
CREATE TABLE IF NOT EXISTS lore_documents (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT,
    parent_document_id TEXT,
    title TEXT NOT NULL,
    slug TEXT,
    document_type TEXT NOT NULL DEFAULT 'PAGE'
        CHECK (document_type IN ('BOOK', 'CHAPTER', 'PAGE', 'RULEBOOK')),
    visibility TEXT NOT NULL DEFAULT 'MASTER'
        CHECK (visibility IN ('MASTER', 'PLAYERS', 'ALL')),
    markdown TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1 CHECK (version >= 1),
    content_hash TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (system_id, campaign_id) REFERENCES campaigns(system_id, id),
    FOREIGN KEY (system_id, parent_document_id) REFERENCES lore_documents(system_id, id),
    UNIQUE (system_id, id),
    CHECK (parent_document_id IS NULL OR parent_document_id <> id)
);

CREATE INDEX IF NOT EXISTS idx_lore_documents_system_slug
    ON lore_documents(system_id, slug);

CREATE INDEX IF NOT EXISTS idx_lore_documents_campaign
    ON lore_documents(campaign_id);

CREATE INDEX IF NOT EXISTS idx_lore_documents_parent
    ON lore_documents(parent_document_id);

CREATE TABLE IF NOT EXISTS lore_links (
    id TEXT PRIMARY KEY,
    document_id TEXT NOT NULL,
    target_type TEXT NOT NULL CHECK (target_type IN (
        'LORE_DOCUMENT', 'ENTITY_INSTANCE', 'ITEM_INSTANCE', 'CAMPAIGN',
        'ATTRIBUTE', 'COMPONENT', 'ENTITY_TEMPLATE', 'CATEGORY', 'ACTION',
        'EVENT', 'RULE', 'SKILL', 'EFFECT', 'ITEM')),
    target_id TEXT NOT NULL,
    label TEXT,
    created_at TEXT NOT NULL,
    FOREIGN KEY (document_id) REFERENCES lore_documents(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_lore_links_document_target
    ON lore_links(document_id, target_type, target_id);

CREATE INDEX IF NOT EXISTS idx_lore_links_target
    ON lore_links(target_type, target_id);

CREATE TABLE IF NOT EXISTS assets (
    id TEXT PRIMARY KEY,
    system_id TEXT,
    campaign_id TEXT,
    owner_type TEXT CHECK (owner_type IN (
        'RPG_SYSTEM', 'CAMPAIGN', 'LOCAL_USER', 'LORE_DOCUMENT',
        'ENTITY_INSTANCE', 'ITEM_INSTANCE', 'ENTITY_TEMPLATE', 'ITEM')),
    owner_id TEXT,
    asset_type TEXT NOT NULL CHECK (asset_type IN ('IMAGE', 'AUDIO', 'MAP', 'TOKEN', 'DOCUMENT')),
    file_path TEXT NOT NULL
        CHECK (length(file_path) > 0
           AND file_path NOT LIKE '/%'          -- absoluto (Unix)
           AND file_path NOT LIKE '_:%'         -- unidade do Windows
           AND file_path NOT LIKE '\%'          -- UNC / absoluto no Windows
           AND file_path NOT LIKE '%..%'),      -- path traversal
    mime_type TEXT,
    size_bytes INTEGER CHECK (size_bytes IS NULL OR size_bytes >= 0),
    content_hash TEXT,
    metadata JSON,
    created_at TEXT NOT NULL,
    updated_at TEXT,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (system_id, campaign_id) REFERENCES campaigns(system_id, id),
    CHECK (system_id IS NOT NULL OR campaign_id IS NOT NULL),
    CHECK ((owner_type IS NULL) = (owner_id IS NULL))
);

CREATE INDEX IF NOT EXISTS idx_assets_system
    ON assets(system_id);

CREATE INDEX IF NOT EXISTS idx_assets_campaign
    ON assets(campaign_id);

CREATE INDEX IF NOT EXISTS idx_assets_owner
    ON assets(owner_type, owner_id);

CREATE TABLE IF NOT EXISTS export_manifests (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    root_type TEXT NOT NULL CHECK (root_type IN (
        'RPG_SYSTEM', 'CAMPAIGN', 'ATTRIBUTE', 'COMPONENT', 'ENTITY_TEMPLATE', 'CATEGORY', 'DICE',
        'PIPELINE', 'ACTION', 'EVENT', 'RULE', 'SKILL', 'EFFECT', 'ITEM',
        'RESOLUTION_POLICY', 'SNAPSHOT_POLICY')),
    root_id TEXT NOT NULL,
    format TEXT NOT NULL DEFAULT 'msrpkg' CHECK (format IN ('msrpkg')),
    format_version INTEGER NOT NULL DEFAULT 1 CHECK (format_version >= 1),
    engine_version TEXT NOT NULL
        CHECK (engine_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND engine_version NOT GLOB '*[^0-9.]*'
               AND length(engine_version) - length(replace(engine_version, '.', '')) = 2),
    content_version TEXT NOT NULL
        CHECK (content_version GLOB '[0-9]*.[0-9]*.[0-9]*' AND content_version NOT GLOB '*[^0-9.]*'
               AND length(content_version) - length(replace(content_version, '.', '')) = 2),
    manifest_json JSON NOT NULL,
    created_at TEXT NOT NULL,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE INDEX IF NOT EXISTS idx_export_manifests_system_root
    ON export_manifests(system_id, root_type, root_id);

CREATE TABLE IF NOT EXISTS import_conflicts (
    id TEXT PRIMARY KEY,
    manifest_id TEXT NOT NULL,
    existing_definition_type TEXT NOT NULL CHECK (existing_definition_type IN (
        'ATTRIBUTE', 'COMPONENT', 'ENTITY_TEMPLATE', 'CATEGORY', 'DICE', 'PIPELINE', 'ACTION',
        'EVENT', 'RULE', 'SKILL', 'EFFECT', 'ITEM', 'RESOLUTION_POLICY', 'SNAPSHOT_POLICY')),
    existing_definition_id TEXT NOT NULL,
    incoming_definition_id TEXT NOT NULL,
    conflict_reason TEXT NOT NULL
        CHECK (conflict_reason IN ('HASH_MISMATCH', 'KEY_COLLISION', 'MISSING_DEPENDENCY')),
    resolution TEXT CHECK (resolution IN ('SKIP', 'OVERWRITE', 'CREATE_COPY')),   -- NULL = pendente
    resolved_at TEXT,
    FOREIGN KEY (manifest_id) REFERENCES export_manifests(id) ON DELETE CASCADE,
    CHECK ((resolution IS NULL) = (resolved_at IS NULL))
);

CREATE INDEX IF NOT EXISTS idx_import_conflicts_manifest
    ON import_conflicts(manifest_id);
-- MSRpg — Lore, Library, Assets e Export/Import
-- Contexto: documentos narrativos, links, assets externos e pacotes exportáveis.

CREATE TABLE IF NOT EXISTS lore_documents (
    id TEXT PRIMARY KEY,
    system_id TEXT NOT NULL,
    campaign_id TEXT,
    parent_document_id TEXT,
    title TEXT NOT NULL,
    slug TEXT,
    document_type TEXT NOT NULL DEFAULT 'page',
    visibility TEXT NOT NULL DEFAULT 'master',
    markdown TEXT,
    data JSON,
    version INTEGER NOT NULL DEFAULT 1,
    content_hash TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id),
    FOREIGN KEY (parent_document_id) REFERENCES lore_documents(id)
);

CREATE INDEX IF NOT EXISTS idx_lore_documents_system_slug
    ON lore_documents(system_id, slug);

CREATE INDEX IF NOT EXISTS idx_lore_documents_campaign
    ON lore_documents(campaign_id);

CREATE TABLE IF NOT EXISTS lore_links (
    id TEXT PRIMARY KEY,
    document_id TEXT NOT NULL,
    target_type TEXT NOT NULL,
    target_id TEXT NOT NULL,
    label TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (document_id) REFERENCES lore_documents(id)
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_lore_links_document_target
    ON lore_links(document_id, target_type, target_id);

CREATE TABLE IF NOT EXISTS assets (
    id TEXT PRIMARY KEY,
    system_id TEXT,
    campaign_id TEXT,
    owner_type TEXT,
    owner_id TEXT,
    asset_type TEXT NOT NULL,
    file_path TEXT NOT NULL,
    mime_type TEXT,
    size_bytes INTEGER,
    content_hash TEXT,
    metadata JSON,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
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
    root_type TEXT NOT NULL,
    root_id TEXT NOT NULL,
    format TEXT NOT NULL DEFAULT 'msrpkg/1.0',
    manifest_json JSON NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (system_id) REFERENCES rpg_systems(id)
);

CREATE INDEX IF NOT EXISTS idx_export_manifests_system_root
    ON export_manifests(system_id, root_type, root_id);

CREATE TABLE IF NOT EXISTS import_conflicts (
    id TEXT PRIMARY KEY,
    manifest_id TEXT NOT NULL,
    existing_definition_type TEXT NOT NULL,
    existing_definition_id TEXT NOT NULL,
    incoming_definition_id TEXT NOT NULL,
    conflict_reason TEXT NOT NULL,
    resolution TEXT,
    resolved_at DATETIME,
    FOREIGN KEY (manifest_id) REFERENCES export_manifests(id)
);

CREATE INDEX IF NOT EXISTS idx_import_conflicts_manifest
    ON import_conflicts(manifest_id);

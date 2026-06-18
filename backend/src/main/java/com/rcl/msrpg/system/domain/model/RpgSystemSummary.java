package com.rcl.msrpg.system.domain.model;

import java.time.Instant;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;

/** Modelo simplificado de RpgSystem. 
 *  para consultas que não vão precisar do settings. 
 */
public record RpgSystemSummary(
    RpgSystemId id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    SyncPolicy syncPolicy,
    Instant createdAt,
    Instant updatedAt
) {

}

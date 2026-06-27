package com.rcl.msrpg.system.infrastructure.web.dto;

import java.time.Instant;

public record RpgSystemSummaryResponse( 
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String syncPolicy,
    Instant createdAt,
    Instant updatedAt
) {

}

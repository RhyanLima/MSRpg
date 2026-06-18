package com.rcl.msrpg.system.application.dto;

import java.time.Instant;

public record RpgSystemSummaryResult(
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

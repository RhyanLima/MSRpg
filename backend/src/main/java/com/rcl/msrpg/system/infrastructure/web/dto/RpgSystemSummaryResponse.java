package com.rcl.msrpg.system.infrastructure.web.dto;

public record RpgSystemSummaryResponse(
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String createdAt,
    String updatedAt
) {
}


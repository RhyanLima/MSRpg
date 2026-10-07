package com.rcl.msrpg.system.infrastructure.persistence;

public record RpgSystemSummaryRow(
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String createdAt,
    String updatedAt
) {
}

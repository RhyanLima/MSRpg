package com.rcl.msrpg.system.application.dto;

public record RpgSystemFilterCommand(
    String name,
    String engineVersion,
    String contentVersion,
    String defaultSyncPolicy
) {

    public static RpgSystemFilterCommand none() {
        return new RpgSystemFilterCommand(null, null, null, null);
    }
}

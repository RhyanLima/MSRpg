package com.rcl.msrpg.shared.identifier;

import java.util.UUID;

public class ResolutionPolicyId extends Identifier<ResolutionPolicyId> {

    public ResolutionPolicyId(UUID value) {
        super(value, Type.RESOLUTION_POLICY);
    }

    public static ResolutionPolicyId generate() {
        return new ResolutionPolicyId(UUID.randomUUID());
    }

    public static ResolutionPolicyId of(UUID value) {
        return new ResolutionPolicyId(value);
    }

}

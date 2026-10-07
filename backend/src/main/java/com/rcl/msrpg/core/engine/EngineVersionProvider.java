package com.rcl.msrpg.core.engine;

import com.rcl.msrpg.core.valueobject.SemanticVersion;

@FunctionalInterface
public interface EngineVersionProvider {

    SemanticVersion current();

}

package com.rcl.msrpg.shared.log;

public enum LogLevel {

    DEBUG(0),
    INFO(1),
    WARN(2),
    ERROR(3),
    FATAL(4);
    
    private final int severity;
    
    LogLevel(int severity) {
        this.severity = severity;
    }
    
    public int getSeverity() {
        return severity;
    }
    
    public boolean isHigherOrEqual(LogLevel other) {
        return this.severity >= other.severity;
    }

}

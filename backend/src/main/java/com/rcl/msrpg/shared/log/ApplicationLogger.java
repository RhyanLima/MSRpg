package com.rcl.msrpg.shared.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLogger {

    private static final Logger log = LoggerFactory.getLogger("APPLICATION");
    private static final String LOGGER_NAME = "APPLICATION";

    public void info(String message) {
        log.info(message);
    }

    public void debug(String message) {
        log.debug(message);
    }

    public void warn(String message) {
        log.warn(message);
    }

    public void warn(String mensage, Object... args) {
        log.warn(mensage, args);
    }

    public void error(String message) {
        log.error(message);
    }

    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }
    
    public boolean isEnabled(LogLevel level) {
        return switch (level) {
            case DEBUG -> log.isDebugEnabled();
            case INFO -> log.isInfoEnabled();
            case WARN -> log.isWarnEnabled();
            case ERROR, FATAL -> log.isErrorEnabled();
        };
    }
    
    public String getName() {
        return LOGGER_NAME;
    }

}

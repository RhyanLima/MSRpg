package com.rcl.msrpg.bootstrap;

public class ShutdownHook {

    private ShutdownHook() {}

    public static void register(Javalin app) {
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }, "msrpg-shutdown-hook"));
    }

}

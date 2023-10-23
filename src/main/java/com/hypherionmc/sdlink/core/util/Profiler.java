package com.hypherionmc.sdlink.core.util;

import com.hypherionmc.sdlink.core.config.SDLinkConfig;
import com.hypherionmc.sdlink.core.discord.BotController;

import java.util.concurrent.TimeUnit;

public class Profiler {

    private long startTime;
    private final String profilerName;
    private boolean hasStarted = false;
    private String message = "";

    private Profiler(String profilerName) {
        this.profilerName = profilerName;
        this.hasStarted = false;
    }

    public static Profiler getProfiler(String name) {
        return new Profiler(name);
    }

    public void start(String message) {
        if (!SDLinkConfig.INSTANCE.generalConfig.debugging)
            return;

        this.message = message;
        this.startTime = System.nanoTime();
        this.hasStarted = true;
    }

    public void stop() {
        if (!SDLinkConfig.INSTANCE.generalConfig.debugging)
            return;

        if (!hasStarted) {
            BotController.INSTANCE.getLogger().error("[Profiler (" + this.profilerName + ")] was not started");
            return;
        }

        long stopTime = System.nanoTime();
        BotController.INSTANCE.getLogger().info("[Profiler (" + this.profilerName + ")] " + message + " took " + TimeUnit.SECONDS.toSeconds(stopTime - startTime) + " seconds");
        hasStarted = false;
    }

}

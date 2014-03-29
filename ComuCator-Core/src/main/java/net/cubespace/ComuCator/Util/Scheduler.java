package net.cubespace.ComuCator.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Scheduler {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static ScheduledFuture<?> schedule(final Runnable runnable, long millis) {
        return executor.schedule(runnable, millis, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(final Runnable runnable, long delay, long interval) {
        return executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
    }
}

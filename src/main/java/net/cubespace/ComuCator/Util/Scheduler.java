package net.cubespace.ComuCator.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Scheduler {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private static final ScheduledExecutorService fastExec = Executors.newScheduledThreadPool(30);

    public static ScheduledFuture<?> schedule(Runnable runnable, long millis) {
        if (millis < 20) {
            return fastExec.schedule(runnable, millis, TimeUnit.MILLISECONDS);
        }

        return executor.schedule(runnable, millis, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long delay, long interval) {
        if (delay < 20) {
            return fastExec.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
        }

        return executor.scheduleAtFixedRate(runnable, delay, interval, TimeUnit.MILLISECONDS);
    }
}

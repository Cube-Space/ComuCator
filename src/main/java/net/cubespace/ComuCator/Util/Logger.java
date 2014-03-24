package net.cubespace.ComuCator.Util;

import java.util.logging.Level;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class Logger {
    /**
     * Logs a Message to the Console. If the Exception given is not null it will get pretty printed onto the Console
     *
     * @param level Which Loglevel should be used for this Message
     * @param message The Message which should be printed
     * @param e Maybe an Exception to print under the message (can be null)
     */
    public static void log(Level level, String message, Exception e) {
        System.out.println("[" + Thread.currentThread().getName() + "] [" + level.getName() + "] " + message);

        if (e != null) {
            writeExceptionToConsole(level.getName(), e);
        }
    }

    private static void writeExceptionToConsole(String logLevel, Throwable throwable) {
        System.out.println("  [" + Thread.currentThread().getName() + "] [" + logLevel + "] Exception in Thread \"" + Thread.currentThread().getName() + "\" " + throwable.getClass().getName() + ": " + throwable.getMessage());

        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for(StackTraceElement element : stackTraceElements) {
            System.out.println("  [" + Thread.currentThread().getName() + "] [" + logLevel + "]   at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() +":" + element.getLineNumber() +")");
        }

        //Print all Exceptions which are appended on this one
        if(throwable.getCause() != null) {
            System.out.println("  [" + Thread.currentThread().getName() + "] [" + logLevel + "] caused by: ");
            writeExceptionToConsole(logLevel, throwable.getCause());
        }
    }

    public static void debug(String message) {
        log(Level.FINEST, message, null);
    }

    public static void debug(String message, Exception e) {
        log(Level.FINEST, message, e);
    }

    public static void info(String message) {
        log(Level.INFO, message, null);
    }

    public static void info(String message, Exception e) {
        log(Level.INFO, message, e);
    }

    public static void warn(String message) {
        log(Level.WARNING, message, null);
    }

    public static void warn(String message, Exception e) {
        log(Level.WARNING, message, e);
    }

    public static void error(String message) {
        log(Level.SEVERE, message, null);
    }

    public static void error(String message, Exception e) {
        log(Level.SEVERE, message, e);
    }
}

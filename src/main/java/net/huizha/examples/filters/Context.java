package net.huizha.examples.filters;

/**
 * Defines a set of methods that a service uses to communicate with its container.
 *
 * @author Zhahui
 *
 */
public interface Context {
    /**
     * Writes an explanatory message and a stack trace for a given Throwable exception to the log file.
     *
     * @param message a String that describes the error or exception
     * @param throwable the Throwable error or exception
     */
    public void log(String message, Throwable throwable);

}

package net.huizha.examples.filters;

import java.util.Enumeration;

/**
 * A filter configuration object used by a container to pass information to a filter during initialization.
 *
 * @see <a
 * href=https://javaee.github.io/javaee-spec/javadocs/javax/servlet/FilterConfig.html>javax.servlet.FilterConfig</a>
 * @author Zhahui
 *
 */
public interface FilterConfig {
    /**
     * Returns the name of this filter as defined in the deployment descriptor.
     *
     * @return the filter name of this filter
     */
    public String getFilterName();

    /**
     * Returns a String containing the value of the named initialization parameter, or null if the initialization
     * parameter does not exist.
     *
     * @param name a String specifying the name of the initialization parameter
     * @return a String containing the value of the initialization parameter, or null if the initialization parameter
     * does not exist
     */
    public String getInitParameter(String name);

    /**
     * Returns the names of the filter's initialization parameters as an Enumeration of String objects, or an empty
     * Enumeration if the filter has no initialization parameters.
     *
     * @return an Enumeration of String objects containing the names of the filter's initialization parameters
     */
    public Enumeration<String> getInitParameterNames();

    /**
     * Returns a reference to the Context in which the caller is executing.
     *
     * @return a Context object, used by the caller to interact with its service's container
     */
    public Context getContext();
}

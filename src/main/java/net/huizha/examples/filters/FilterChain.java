package net.huizha.examples.filters;

import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * A FilterChain is an object provided by the container to the developer giving a view into the invocation chain of a
 * filtered request for a resource. Filters use the FilterChain to invoke the next filter in the chain, or if the
 * calling filter is the last filter in the chain, to invoke the resource at the end of the chain.
 *
 * @see <a
 * href=https://javaee.github.io/javaee-spec/javadocs/javax/servlet/FilterChain.html>javax.servlet.FilterChain</a>
 *
 * @author Zhahui
 *
 */
public interface FilterChain {
    /**
     * Causes the next filter in the chain to be invoked, or if the calling filter is the last filter in the chain,
     * causes the resource at the end of the chain to be invoked.
     *
     * @param request the request to pass along the chain
     * @param response the response to pass along the chain
     * @throws FilterException if an exception has occurred that interferes with the filterChain's normal operation
     */
    public void doFilter(Request request, Response response);

}

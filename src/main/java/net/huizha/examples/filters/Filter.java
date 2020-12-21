package net.huizha.examples.filters;

import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * Define intercepting filter interface that performs filtering tasks on either the request to a resource, or on the
 * response from a resource, or both.
 *
 * @see <a href=https://javaee.github.io/javaee-spec/javadocs/javax/servlet/Filter.html>javax.servlet.Filter</a>
 */
public interface Filter {
    /**
     * Initialize the filter. Called by the container exactly once after instantiating the filter to indicate to a
     * filter that it is being placed into service. The init method must complete successfully before the filter is
     * asked to do any filtering work.
     * <p>
     * The container cannot place the filter into service if the init method either:
     * <ol>
     * <li>Throws a ServletException
     * <li>Does not return within a time period defined by the container
     * </ol>
     *
     * @param filterConfig a FilterConfig object containing the filter's configuration and initialization parameters
     * @throws FilterException if an exception has occurred that interferes with the filter's normal operation
     */
    public void init(FilterConfig filterConfig);

    /**
     * Execute the filter function. The doFilter method is called by the container each time a request/response pair is
     * passed through the chain due to a client request for a resource at the end of the chain.
     * <p>
     * The doFilter method of the Filter is called by the container each time a request/response pair is passed through
     * the chain due to a client request for a resource at the end of the chain. The FilterChain passed in to this
     * method allows the Filter to pass on the request and response to the next entity in the chain.
     *
     * @param request the ServletRequest object contains the client's request
     * @param response the ServletResponse object contains the filter's response
     * @param chain the FilterChain for invoking the next filter or the resource
     * @throws FilterException if an exception occurs that interferes with the filter's normal operation
     */
    public void doFilter(Request request, Response response, FilterChain chain);

    /**
     * Clean up the filter. Called by the container to indicate to a filter that it is being taken out of service.
     * <p>
     * This method is only called once all threads within the filter's doFilter method have exited or after a timeout
     * period has passed. After the container calls this method, it will not call the doFilter method again on this
     * instance of the filter.
     * <p>
     * This method gives the filter an opportunity to clean up any resources that are being held (for example, memory,
     * file handles, threads) and make sure that any persistent state is synchronized with the filter's current state in
     * memory.
     */
    public void destroy();
}

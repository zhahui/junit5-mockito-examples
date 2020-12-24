package net.huizha.examples.junit5_mockito;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.huizha.examples.filters.Filter;
import net.huizha.examples.filters.FilterChain;
import net.huizha.examples.filters.FilterConfig;
import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * Define a session filter.
 *
 * @author Zhahui
 *
 */
public class SessionFilter implements Filter {
    private static final String LOG_MSG_TAG = SessionFilter.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SessionFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.traceEntry();
        LOGGER.info("{}: Successfully initialized filter", LOG_MSG_TAG);
        LOGGER.traceExit();
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        LOGGER.traceEntry();
        LOGGER.info("{}: Successfully performed filter function", LOG_MSG_TAG);
        LOGGER.traceExit();
    }

    @Override
    public void destroy() {
        LOGGER.traceEntry();
        LOGGER.info("{}: Successfully destroyed filter", LOG_MSG_TAG);
        LOGGER.traceExit();
    }
}

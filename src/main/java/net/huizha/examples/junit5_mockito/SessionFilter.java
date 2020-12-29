package net.huizha.examples.junit5_mockito;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.huizha.examples.filters.Filter;
import net.huizha.examples.filters.FilterChain;
import net.huizha.examples.filters.FilterConfig;
import net.huizha.examples.filters.FilterException;
import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * Define a session filter.
 *
 * @author Zhahui
 *
 */
public class SessionFilter implements Filter {
    private static final String JNDI_OIDC_SESSION_MANAGEMENT_CONFIG = "java:comp/env/config/OidcSessionManagementConfig";
    private static final Logger LOGGER = LogManager.getLogger(SessionFilter.class);

    private InitialContext initialContext;
    private String baseUrl;
    private String validateSessionContextPath;
    private String refreshSessionContextPath;

    public SessionFilter() {
        initialContext = null;
        baseUrl = null;
        validateSessionContextPath = null;
        refreshSessionContextPath = null;
    }

    protected void setInitialContext(InitialContext initialContext) {
        this.initialContext = initialContext;
    }

    /**
     * @return the baseUrl
     */
    protected String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return the validateSessionContextPath
     */
    protected String getValidateSessionContextPath() {
        return validateSessionContextPath;
    }

    /**
     * @return the refreshSessionContextPath
     */
    protected String getRefreshSessionContextPath() {
        return refreshSessionContextPath;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.traceEntry();
        OidcSessionManagementConfig oidcSessionManagementConfig = null;
        if (filterConfig == null) {
            throw new FilterException(new IllegalArgumentException("FilterConfig is null"));
        }

        try {
            if (initialContext == null) {
                // This constructor could throw exception too but Mockito can not mock creating object with new
                // operator.
                initialContext = new InitialContext();
            }
            oidcSessionManagementConfig = (OidcSessionManagementConfig) initialContext
                    .lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG);
        } catch (NamingException e) {
            LOGGER.error(String.format("Error occurred when looking up the named object: %s",
                    JNDI_OIDC_SESSION_MANAGEMENT_CONFIG), e);
            throw new FilterException(e);
        }

        if (oidcSessionManagementConfig == null) {
            String erroMsg = String.format("Could not find the named object: %s", JNDI_OIDC_SESSION_MANAGEMENT_CONFIG);
            LOGGER.error(erroMsg);
            throw new FilterException(erroMsg);
        }

        baseUrl = oidcSessionManagementConfig.getBaseUrl();
        if (StringUtils.isBlank(baseUrl)) {
            throw new FilterException("baseUrl is null or empty");
        }
        validateSessionContextPath = oidcSessionManagementConfig.getValidateSessionContextPath();
        if (StringUtils.isBlank(validateSessionContextPath)) {
            throw new FilterException("validateSessionContextPath is null or empty");
        }
        refreshSessionContextPath = oidcSessionManagementConfig.getRefreshSessionContextPath();
        if (StringUtils.isBlank(refreshSessionContextPath)) {
            throw new FilterException("refreshSessionContextPath is null or empty");
        }

        LOGGER.info(
                "OIDC session management configuration parameters: baserUrl={}, validateSessionContextPath={}, refreshSessionContextPath={}",
                baseUrl, validateSessionContextPath, refreshSessionContextPath);
        LOGGER.info("Successfully initialized filter");
        LOGGER.traceExit();
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain chain) {
        LOGGER.traceEntry();
        LOGGER.info("Successfully performed filter function");
        LOGGER.traceExit();
    }

    @Override
    public void destroy() {
        LOGGER.traceEntry();
        LOGGER.info("Successfully destroyed filter");
        LOGGER.traceExit();
    }
}

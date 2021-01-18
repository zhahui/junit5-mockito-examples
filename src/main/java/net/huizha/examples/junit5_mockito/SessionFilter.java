package net.huizha.examples.junit5_mockito;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

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
    private static final String OIDC_ACCESS_TOKEN_KEY = "oidc-access-token";
    private static final String JNDI_OIDC_SESSION_MANAGEMENT_CONFIG = "java:comp/env/config/OidcSessionManagementConfig";
    private static final String NOT_AVAILABLE = "N/A";
    private static final String OIDC_LAST_REFRESHED_DATETIME_KEY = "oidc-last-refreshed-datetime";
    private static final long ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS = 30;
    private static final String OIDC_CLAIM_SUB_KEY = "sub";
    private static final String OIDC_SESSION_REF_KEY = "oidc-session-ref";
    private static final String OIDC_CLIENT_ID_KEY = "client-id";
    private static final int REST_API_CALL_TIMEOUT_IN_MILLISECONDS = 3000;
    private static final String ERROR_MSG_VALIDATE_REFRESH_SESSION = "Error occurred when validating and refreshing OIDC session: %s";
    private static final Logger LOGGER = LogManager.getLogger(SessionFilter.class);

    private InitialContext initialContext;
    private String baseUrl;
    private String validateSessionContextPath;
    private String refreshSessionContextPath;
    private String oidcClientId;

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
        try {
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
                String erroMsg = String.format("Could not find the named object: %s",
                    JNDI_OIDC_SESSION_MANAGEMENT_CONFIG);
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
            oidcClientId = oidcSessionManagementConfig.getOidcClientId();
            if (StringUtils.isBlank(oidcClientId)) {
                throw new FilterException("oidcClientId is null or empty");
            }

            LOGGER.info(
                "OIDC session management configuration parameters: baserUrl={}, validateSessionContextPath={}, refreshSessionContextPath={}",
                baseUrl, validateSessionContextPath, refreshSessionContextPath);
            LOGGER.info("Successfully initialized filter");
        } finally {
            LOGGER.traceExit();
        }
    }

    @Override
    public void doFilter(Request request, Response response, FilterChain filterChain) {
        LOGGER.traceEntry();

        if (request == null) {
            throw new FilterException(new NullPointerException("Request is null"));
        }
        String sub = NOT_AVAILABLE;
        String oidcSessionRef = NOT_AVAILABLE;
        try {
            HttpSession httpSession = request.getSession();
            String accessToken = (String) httpSession.getAttribute(OIDC_ACCESS_TOKEN_KEY);
            if (accessToken == null) {
                LOGGER.warn(
                    "Could not retrieve access token. Container HTTP session may be expired. No OIDC session refreshing");
                return;
            }
            String idToken = OidcClientHelper.getIdTokenFromAccessToken(accessToken);
            Map<String, String> claimsMap = OidcClientHelper.getJwtClaimsFromIdTokenAsMap(idToken);
            sub = claimsMap.getOrDefault(OIDC_CLAIM_SUB_KEY, NOT_AVAILABLE);
            oidcSessionRef = claimsMap.getOrDefault(OIDC_SESSION_REF_KEY, NOT_AVAILABLE);
            if (isTimeToRefreshOidcSession(httpSession)) {
                LOGGER.info("It's time to try refreshing OIDC session (Last refreshed: {}). sub={}, OidcSessionRef={}",
                    httpSession.getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY), sub, oidcSessionRef);
                boolean isOidcSessionValid = validateOidcSession(sub, oidcSessionRef);
                LOGGER.info("isOidcSessionValid: {}. sub={}, OidcSessionRef={}", isOidcSessionValid, sub,
                    oidcSessionRef);
                if (isOidcSessionValid) {
                    tryRefreshOidcSession(sub, oidcSessionRef, httpSession);
                } else {
                    LOGGER.warn(
                        "OIDC session is not valid anymore. No OIDC session refreshing. sub={}, OidcSessionRef={}", sub,
                        oidcSessionRef);
                }
            } else {
                LOGGER.info(
                    "OIDC session has been refreshed recently (Last refreshed: {}). No need to refresh at this time. sub={}, OidcSessionRef={}",
                    httpSession.getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY), sub, oidcSessionRef);
            }
            LOGGER.info("Successfully performed filter function");
        } catch (NullPointerException npe) {
            LOGGER.error(String.format(ERROR_MSG_VALIDATE_REFRESH_SESSION,
                "Container HTTP session is null. Session may be expired"), npe);
        } catch (IllegalArgumentException iae) {
            LOGGER.error(String.format(ERROR_MSG_VALIDATE_REFRESH_SESSION, iae.getMessage()), iae);
        } catch (Exception e) {
            LOGGER.error(
                String.format(ERROR_MSG_VALIDATE_REFRESH_SESSION, "sub={}, OidcSessionRef={}", sub, oidcSessionRef), e);
        } finally {
            filterChain.doFilter(request, response);
            LOGGER.traceExit();
        }
    }

    @Override
    public void destroy() {
        LOGGER.traceEntry();
        LOGGER.info("Successfully destroyed filter");
        LOGGER.traceExit();
    }

    /**
     * Determine if it is time to refresh OIDC session.
     *
     * @param httpSession the HTTP session object
     * @return true if it is time to refresh OIDC session; false otherwise
     */
    private boolean isTimeToRefreshOidcSession(HttpSession httpSession) {
        if (httpSession == null) {
            return false;
        }

        LocalDateTime lastRefreshedDateTime = (LocalDateTime) httpSession
            .getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY);

        // lastRefreshedDateTime==null means this is the first time the session was created, so no need to refresh
        // session
        if (lastRefreshedDateTime == null) {
            httpSession.setAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY, LocalDateTime.now());
            return false;
        } else {
            return lastRefreshedDateTime.plusSeconds(ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS)
                .isBefore(LocalDateTime.now());
        }
    }

    private boolean validateOidcSession(String sub, String oidcSessionRef) {
        try {
            String response = org.apache.http.client.fluent.Request.Post(baseUrl + validateSessionContextPath)
                .addHeader(OIDC_SESSION_REF_KEY, oidcSessionRef).addHeader(OIDC_CLIENT_ID_KEY, oidcClientId)
                .connectTimeout(REST_API_CALL_TIMEOUT_IN_MILLISECONDS).execute().returnContent().asString();
            LOGGER.debug("session validation response={}, oidcSessionRef={}", response, oidcSessionRef);
            return response.contains("true");
        } catch (IOException e) {
            LOGGER.error("Error occurs when validating CIAM session: oidcSessionRef={}", oidcSessionRef);
            LOGGER.error(e);
            return false;
        }
    }

    private void tryRefreshOidcSession(String sub, String oidcSessionRef, HttpSession httpSession) {
        boolean isSessionRefreshed = false;
        try {
            String response = org.apache.http.client.fluent.Request.Post(baseUrl + refreshSessionContextPath)
                .addHeader(OIDC_SESSION_REF_KEY, oidcSessionRef).addHeader(OIDC_CLIENT_ID_KEY, oidcClientId)
                .connectTimeout(REST_API_CALL_TIMEOUT_IN_MILLISECONDS).execute().returnContent().asString();
            httpSession.setAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY, LocalDateTime.now());
            LOGGER.debug("session refreshing response={}, ciamSessionRef={}", response, oidcSessionRef);
            if (response != null && !response.contains("\"valid\": false")) {
                isSessionRefreshed = true;
            }
        } catch (IOException e) {
            LOGGER.error("Error occurs when refreshing CIAM session: ciamSessionRef={}", oidcSessionRef);
            LOGGER.error(e);
        }

        if (isSessionRefreshed) {
            LOGGER.info("OIDC session has been successfully refreshed. sub={}, oidcSessionRef={}", sub, oidcSessionRef);
        } else {
            LOGGER.info("OIDC session was NOT refreshed. sub={}, oidcSessionRef={}", sub, oidcSessionRef);
        }
    }
}

package net.huizha.examples.junit5_mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import net.huizha.examples.filters.FilterChain;
import net.huizha.examples.filters.FilterConfig;
import net.huizha.examples.filters.FilterException;
import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * Static-mock test class.
 *
 * @author Zhahui
 *
 */
class SessionFilterStaticMockTest {
    private static final String OIDC_ACCESS_TOKEN_KEY = "oidc-access-token";
    private static final String JNDI_OIDC_SESSION_MANAGEMENT_CONFIG = "java:comp/env/config/OidcSessionManagementConfig";
    private static final String OIDC_LAST_REFRESHED_DATETIME_KEY = "oidc-last-refreshed-datetime";
    private static final String FAKED_BASE_URL = "https://faked-url";
    private static final String FAKED_VALIDATE_SESSION_CONTEXTPATH = "faked-validate-session-context-path";
    private static final String FAKED_REFRESH_SESSION_CONTEXTPATH = "faked-refresh-session-context-path";
    private static final String FAKED_OIDC_CLIENT_ID = "faked-oidc-client-id";
    private static final String FAKED_OIDC_ACCESS_TOKEN_STRING = "faked-access-token-string";
    private static final String NOT_AVAILABLE = "N/A";
    private static final String ERROR_MSG_VALIDATE_REFRESH_SESSION = "Error occurred when validating and refreshing OIDC session: %s";
    // Class under test
    private SessionFilter filterToTest;

    private static Logger mockedLogger;
    private FilterConfig mockedFilterConfig;
    private InitialContext mockedContext;
    private OidcSessionManagementConfig mockedOidcSessionManagementConfig;
    private Request mockedRequest;
    private Response mockedResponse;
    private FilterChain mockedFilterChain;
    private HttpSession mockedHttpSession;
    private String sub;
    private String oidcSessionRef;

    @BeforeAll
    static void setUpBeforeAll() {
        mockedLogger = mock(Logger.class);
    }

    @BeforeEach
    void setUpBeforeEach() {
        mockedFilterConfig = mock(FilterConfig.class);
        mockedContext = mock(InitialContext.class);
        mockedOidcSessionManagementConfig = mock(OidcSessionManagementConfig.class);
        mockedRequest = mock(Request.class);
        mockedResponse = mock(Response.class);
        mockedFilterChain = mock(FilterChain.class);
        mockedHttpSession = mock(HttpSession.class);
        sub = NOT_AVAILABLE;
        oidcSessionRef = NOT_AVAILABLE;
    }

    @AfterEach
    void cleanUpAfterEach() {
        reset(mockedLogger);
    }

    /**
     * Regular style of testing.
     */
    @Test
    void destroy_ShouldCallLog_Regular() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            filterToTest.destroy();

            // regular style of verification
            verify(mockedLogger, times(1)).info("Successfully destroyed filter");
        }
    }

    /**
     * BDD style of testing.
     */
    @Test
    void destroy_ShouldCallLog_Bdd() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            filterToTest.destroy();

            // BDD style of verification
            then(mockedLogger).should(times(1)).info("Successfully destroyed filter");
        }
    }

    /**
     * Using JUnit 5 (Jupiter) assertion to assert exceptions.
     */
    @Test
    void init_ShouldThrowException_WhenContextLookUpFailed_UsingJupiterAssertionToAssertException() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Make sure "new InitialContext()" will be invoked
            filterToTest.setInitialContext(null);

            Exception exception = Assertions.assertThrows(FilterException.class, () -> {
                filterToTest.init(mockedFilterConfig);
            });
            assertThat(exception.getCause()).isInstanceOf(NamingException.class);
            assertThat(exception.getMessage()).contains("javax.naming.NoInitialContextException");
        }
    }

    /**
     * Using AssertJ to assert exceptions in fluent way.
     */
    @Test
    void init_ShouldThrowException_WhenContextLookUpFailed_UsingAssertJToAssertException() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Make sure "new InitialContext()" will be invoked
            filterToTest.setInitialContext(null);

            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasCauseInstanceOf(NamingException.class)
                .hasCauseInstanceOf(NoInitialContextException.class)
                .hasMessageContaining("javax.naming.NoInitialContextException");
            then(mockedLogger).should(times(1)).error(eq(String
                .format("Error occurred when looking up the named object: %s", JNDI_OIDC_SESSION_MANAGEMENT_CONFIG)),
                any(NamingException.class));
        }
    }

    @Test
    void init_ShouldThrowException_WhenContextLookUpReturnsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG)).willReturn(null);

            String erroMsg = String.format("Could not find the named object: %s", JNDI_OIDC_SESSION_MANAGEMENT_CONFIG);
            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasMessageMatching(erroMsg);
            then(mockedLogger).should(times(1)).error(erroMsg);
        }
    }

    @Test
    void init_ShouldThrowException_WhenBaseUrlIsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG))
                .willReturn(mockedOidcSessionManagementConfig);
            given(mockedOidcSessionManagementConfig.getBaseUrl()).willReturn(null);

            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasMessageMatching("baseUrl is null or empty");
        }
    }

    @Test
    void init_ShouldThrowException_WhenValidateSessionContextPathIsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG))
                .willReturn(mockedOidcSessionManagementConfig);
            given(mockedOidcSessionManagementConfig.getBaseUrl()).willReturn(FAKED_BASE_URL);
            given(mockedOidcSessionManagementConfig.getValidateSessionContextPath()).willReturn(null);

            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasMessageMatching("validateSessionContextPath is null or empty");
        }
    }

    @Test
    void init_ShouldThrowException_WhenRefreshSessionContextPathIsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG))
                .willReturn(mockedOidcSessionManagementConfig);
            given(mockedOidcSessionManagementConfig.getBaseUrl()).willReturn(FAKED_BASE_URL);
            given(mockedOidcSessionManagementConfig.getValidateSessionContextPath())
                .willReturn(FAKED_VALIDATE_SESSION_CONTEXTPATH);
            given(mockedOidcSessionManagementConfig.getRefreshSessionContextPath()).willReturn(null);

            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasMessageMatching("refreshSessionContextPath is null or empty");
        }
    }

    @Test
    void init_ShouldThrowException_WhenOidcClientIdIsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG))
                .willReturn(mockedOidcSessionManagementConfig);
            given(mockedOidcSessionManagementConfig.getBaseUrl()).willReturn(FAKED_BASE_URL);
            given(mockedOidcSessionManagementConfig.getValidateSessionContextPath())
                .willReturn(FAKED_VALIDATE_SESSION_CONTEXTPATH);
            given(mockedOidcSessionManagementConfig.getRefreshSessionContextPath())
                .willReturn(FAKED_REFRESH_SESSION_CONTEXTPATH);
            given(mockedOidcSessionManagementConfig.getOidcClientId()).willReturn(null);

            assertThatThrownBy(() -> {
                filterToTest.init(mockedFilterConfig);
            }).isInstanceOf(FilterException.class).hasMessageMatching("oidcClientId is null or empty");
        }
    }

    @Test
    void init_ShouldWork_WhenContextLookUpSucceeded() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();
            // Mock context so that lookup() can be mocked
            filterToTest.setInitialContext(mockedContext);

            given(mockedContext.lookup(JNDI_OIDC_SESSION_MANAGEMENT_CONFIG))
                .willReturn(mockedOidcSessionManagementConfig);
            given(mockedOidcSessionManagementConfig.getBaseUrl()).willReturn(FAKED_BASE_URL);
            given(mockedOidcSessionManagementConfig.getValidateSessionContextPath())
                .willReturn(FAKED_VALIDATE_SESSION_CONTEXTPATH);
            given(mockedOidcSessionManagementConfig.getRefreshSessionContextPath())
                .willReturn(FAKED_REFRESH_SESSION_CONTEXTPATH);
            given(mockedOidcSessionManagementConfig.getOidcClientId()).willReturn(FAKED_OIDC_CLIENT_ID);

            filterToTest.init(mockedFilterConfig);

            then(mockedLogger).should(times(1)).info(
                "OIDC session management configuration parameters: baserUrl={}, validateSessionContextPath={}, refreshSessionContextPath={}",
                FAKED_BASE_URL, FAKED_VALIDATE_SESSION_CONTEXTPATH, FAKED_REFRESH_SESSION_CONTEXTPATH);
            then(mockedLogger).should(times(1)).info("Successfully initialized filter");
            then(mockedLogger).should(times(1)).traceExit();
            assertThat(filterToTest.getBaseUrl()).isNotBlank().isEqualTo(FAKED_BASE_URL);
            assertThat(filterToTest.getValidateSessionContextPath()).isNotBlank()
                .isEqualTo(FAKED_VALIDATE_SESSION_CONTEXTPATH);
            assertThat(filterToTest.getRefreshSessionContextPath()).isNotBlank()
                .isEqualTo(FAKED_REFRESH_SESSION_CONTEXTPATH);
        }
    }

    @Test
    void doFilter_ShouldNotRefreshSession_WhenContainerHttpSessionIsNull() throws NamingException {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();

            given(mockedRequest.getSession()).willReturn(null);

            filterToTest.doFilter(mockedRequest, mockedResponse, mockedFilterChain);

            then(mockedLogger).should(times(1)).error(eq(String.format(ERROR_MSG_VALIDATE_REFRESH_SESSION,
                "Container HTTP session is null. Session may be expired")), any(NullPointerException.class));
            then(mockedFilterChain).should(times(1)).doFilter(mockedRequest, mockedResponse);
        }
    }

    @Test
    void doFilter_ShouldNotRefreshSession_WhenOidcSessionIsNotValid() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            filterToTest = new SessionFilter();

            given(mockedRequest.getSession()).willReturn(mockedHttpSession);
            given(mockedHttpSession.getAttribute(OIDC_ACCESS_TOKEN_KEY)).willReturn(null);

            filterToTest.doFilter(mockedRequest, mockedResponse, mockedFilterChain);

            then(mockedLogger).should(times(1)).warn(String.format(
                "Could not retrieve access token. Container HTTP session may be expired. No OIDC session refreshing"));
            then(mockedFilterChain).should(times(1)).doFilter(mockedRequest, mockedResponse);
        }
    }

    static Stream<Integer> lessThanOrEqualTo30Provider() {
        return Stream.of(null, 1, 15, 29, 30);
    }

    @ParameterizedTest
    @MethodSource("lessThanOrEqualTo30Provider")
    void doFilter_ShouldNotRefreshSession_WhenContainerHttpSessionHasAccessTokenAndHasLastRefreshedDateTimeLessThanOrEqualTo30Seconds(
        Integer interval) {
        String logMessage = "OIDC session has been refreshed recently (Last refreshed: {}). No need to refresh at this time. sub={}, OidcSessionRef={}";
        lastRefreshedTimeTestHelper(interval, logMessage);
    }

    static Stream<Integer> moreThan30Provider() {
        return Stream.of(31, 50, 120);
    }

    @ParameterizedTest
    @MethodSource("moreThan30Provider")
    void doFilter_ShouldNotRefreshSession_WhenContainerHttpSessionHasAccessTokenAndHasLastRefreshedDateTimeMoreThan30Seconds(
        Integer interval) {
        String logMessage = "It's time to try refreshing OIDC session (Last refreshed: {}). sub={}, OidcSessionRef={}";
        lastRefreshedTimeTestHelper(interval, logMessage);
    }

    private void lastRefreshedTimeTestHelper(Integer interval, String logMessage) {
        sub = "stub-sub";
        oidcSessionRef = "stub-oidc-session-ref";
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime lastRefreshedDateTime = interval == null ? null : currentDateTime.minusSeconds(interval);
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
            MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            mockedLocalDateTime.when(() -> {
                LocalDateTime.now();
            }).thenReturn(currentDateTime);

            filterToTest = new SessionFilter();

            given(mockedRequest.getSession()).willReturn(mockedHttpSession);
            given(mockedHttpSession.getAttribute(OIDC_ACCESS_TOKEN_KEY)).willReturn(FAKED_OIDC_ACCESS_TOKEN_STRING);
            given(mockedHttpSession.getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY)).willReturn(lastRefreshedDateTime);

            filterToTest.doFilter(mockedRequest, mockedResponse, mockedFilterChain);

            then(mockedLogger).should(times(1)).info(logMessage, lastRefreshedDateTime, sub, oidcSessionRef);
            then(mockedFilterChain).should(times(1)).doFilter(mockedRequest, mockedResponse);
            then(mockedLogger).should(times(1)).info("Successfully performed filter function");
        }
    }

    @Test
    void doFilter_ShouldNotRefreshSession_WhenAccessTokenHasNoIdToken() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class);
            MockedStatic<OidcClientHelper> mockedOidcClientHelper = mockStatic(OidcClientHelper.class);) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);

            mockedOidcClientHelper.when(() -> {
                OidcClientHelper.getIdTokenFromAccessToken(anyString());
            }).thenReturn(null);
            mockedOidcClientHelper.when(() -> {
                OidcClientHelper.getJwtClaimsFromIdTokenAsMap(null);
            }).thenThrow(
                new IllegalArgumentException("Could not get JWT claims from ID token: ID token is null or empty"));

            filterToTest = new SessionFilter();

            given(mockedRequest.getSession()).willReturn(mockedHttpSession);
            given(mockedHttpSession.getAttribute(OIDC_ACCESS_TOKEN_KEY)).willReturn(FAKED_OIDC_ACCESS_TOKEN_STRING);

            filterToTest.doFilter(mockedRequest, mockedResponse, mockedFilterChain);

            then(mockedLogger).should(times(1)).error(
                eq(String.format(ERROR_MSG_VALIDATE_REFRESH_SESSION,
                    "Could not get JWT claims from ID token: ID token is null or empty")),
                any(IllegalArgumentException.class));
            then(mockedFilterChain).should(times(1)).doFilter(mockedRequest, mockedResponse);
        }
    }
}

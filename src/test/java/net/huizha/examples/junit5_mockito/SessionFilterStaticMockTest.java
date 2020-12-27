package net.huizha.examples.junit5_mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import net.huizha.examples.filters.FilterConfig;
import net.huizha.examples.filters.FilterException;

/**
 * Static-mock test class.
 *
 * @author Zhahui
 *
 */
class SessionFilterStaticMockTest {
    private static final String JNDI_OIDC_SESSION_MANAGEMENT_CONFIG = "java:comp/env/config/OidcSessionManagementConfig";
    // Class under test
    private SessionFilter filterToTest;

    private static Logger mockedLogger;
    private FilterConfig mockedFilterConfig;
    private InitialContext mockedContext;

    @BeforeAll
    static void setUpBeforeAll() {
        mockedLogger = mock(Logger.class);
    }

    @BeforeEach
    void setUpBeforeEach() {
        mockedFilterConfig = mock(FilterConfig.class);
        mockedContext = mock(InitialContext.class);
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
            then(mockedLogger).should(times(1)).error(
                    eq(String.format("Error occurred when looking up the named object: %s", JNDI_OIDC_SESSION_MANAGEMENT_CONFIG)),
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
}

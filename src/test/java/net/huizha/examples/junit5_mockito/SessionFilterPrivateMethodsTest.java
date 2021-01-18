package net.huizha.examples.junit5_mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

/**
 * Examples of unit testing private methods.
 *
 * @author Zhahui
 *
 */
class SessionFilterPrivateMethodsTest {
    private static final String OIDC_LAST_REFRESHED_DATETIME_KEY = "oidc-last-refreshed-datetime";
    private static final int ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS = 30;
    private SessionFilter filterToTest;

    @BeforeAll
    static void setUpBeforeAll() {
    }

    @BeforeEach
    void setUpBeforeEach() {
        filterToTest = new SessionFilter();
    }

    @AfterEach
    void cleanUpAfterEach() {
    }

    @Test
    void isTimeToRefreshOidcSession_ShouldReturnFalse_WhenHttpSessionIsNull() throws Exception {
        Method method = SessionFilter.class.getDeclaredMethod("isTimeToRefreshOidcSession", HttpSession.class);
        method.setAccessible(true);

        assertThat((Boolean) method.invoke(filterToTest, (HttpSession) null)).isFalse();
    }

    @Test
    void isTimeToRefreshOidcSession_ShouldReturnFalse_WhenHttpSessionFirstTimeNotHaveLastRefreshedDateTime()
            throws Exception {
        Method method = SessionFilter.class.getDeclaredMethod("isTimeToRefreshOidcSession", HttpSession.class);
        method.setAccessible(true);

        HttpSession mockedHttpSession = mock(HttpSession.class);
        given(mockedHttpSession.getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY)).willReturn(null);

        assertThat((Boolean) method.invoke(filterToTest, mockedHttpSession)).isFalse();
    }

    @Test
    void isTimeToRefreshOidcSession_ShouldReturnFalse_WhenHttpSessionHaveLastRefreshedDateTimeButTooSoonToRefresh1()
            throws Exception {
        isTimeToRefreshOidcSessionTestHelper(ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS, false);
    }

    @Test
    void isTimeToRefreshOidcSession_ShouldReturnFalse_WhenHttpSessionHaveLastRefreshedDateTimeButTooSoonToRefresh2()
            throws Exception {
        isTimeToRefreshOidcSessionTestHelper(ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS-1, false);
    }

    @Test
    void isTimeToRefreshOidcSession_ShouldReturnTrue_WhenHttpSessionHaveLastRefreshedDateTimeAndOver30Seconds()
            throws Exception {
        isTimeToRefreshOidcSessionTestHelper(ODIC_REFRESH_SESSION_WAITING_TIME_IN_SECONDS+1, true);
    }

    private void isTimeToRefreshOidcSessionTestHelper(int refreshWaitingTime, boolean shouldRefresh) throws Exception {
        Method method = SessionFilter.class.getDeclaredMethod("isTimeToRefreshOidcSession", HttpSession.class);
        method.setAccessible(true);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime lastRefreshedDateTime = currentDateTime.minusSeconds(refreshWaitingTime);

        try (MockedStatic<LocalDateTime> mockedLocalDateTime = mockStatic(LocalDateTime.class)) {
            mockedLocalDateTime.when(() -> {
                LocalDateTime.now();
            }).thenReturn(currentDateTime);
            HttpSession mockedHttpSession = mock(HttpSession.class);
            given(mockedHttpSession.getAttribute(OIDC_LAST_REFRESHED_DATETIME_KEY)).willReturn(lastRefreshedDateTime);
            assertThat((Boolean) method.invoke(filterToTest, mockedHttpSession)).isEqualTo(shouldRefresh);
        }
    }
}

package net.huizha.examples.junit5_mockito;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class SessionFilterTest {
    private static final String LOG_MSG_TAG = "SessionFilter";
    private static Logger mockedLogger;
    private static SessionFilter filterToTest;

    @BeforeAll
    static void setUpBeforeAll() {
        mockedLogger = mock(Logger.class);
    }

    @BeforeEach
    void setUpBeforeEach() {
    }

    @AfterEach
    void cleanUpAfterEach() {
        reset(mockedLogger);
    }
    @Test
    void testDestroyShouldCallLog() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);
            filterToTest = new SessionFilter();
            filterToTest.destroy();
            then(mockedLogger).should(times(1)).info("{}: Successfully destroyed filter", LOG_MSG_TAG);
        }
    }

    @Test
    void testDestroyShouldCallLog1() {
        try (MockedStatic<LogManager> mockedLogManager = mockStatic(LogManager.class)) {
            mockedLogManager.when(() -> {
                LogManager.getLogger(SessionFilter.class);
            }).thenReturn(mockedLogger);
            filterToTest = new SessionFilter();
            filterToTest.destroy();
            then(mockedLogger).should(times(1)).info("{}: Successfully destroyed filter", LOG_MSG_TAG);
        }
    }
}

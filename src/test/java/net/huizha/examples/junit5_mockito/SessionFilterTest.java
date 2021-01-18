package net.huizha.examples.junit5_mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.huizha.examples.filters.FilterChain;
import net.huizha.examples.filters.FilterException;
import net.huizha.examples.requestresponse.Request;
import net.huizha.examples.requestresponse.Response;

/**
 * Non-static-mock test class. Non-static-mock test class and static-mock class have to be separated.
 *
 * @author Zhahui
 *
 */
class SessionFilterTest {
    private SessionFilter filterToTest;
    private Request request;
    private Response response;
    private FilterChain filterChain;

    @BeforeAll
    static void setUpBeforeAll() {
    }

    @BeforeEach
    void setUpBeforeEach() {
        filterToTest = new SessionFilter();
        request = null;
        response = null;
        filterChain = null;
    }

    @AfterEach
    void cleanUpAfterEach() {
    }

    @Test
    void init_ShouldThrowException_WhenFilterConfigIsNull() {
        assertThatThrownBy(() -> {
            filterToTest.init(null);
        }).isInstanceOf(FilterException.class).hasCauseExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("FilterConfig is null");
    }

    @Test
    void doFilter_ShouldThrowException_WhenRequestIsNull() {
        request = null;
        assertThatThrownBy(() -> {
            filterToTest.doFilter(request, response, filterChain);
        }).isInstanceOf(FilterException.class).hasCauseExactlyInstanceOf(NullPointerException.class)
            .hasMessageContaining("Request is null");
    }
}

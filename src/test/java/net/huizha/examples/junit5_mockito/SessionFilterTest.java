package net.huizha.examples.junit5_mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.huizha.examples.filters.FilterException;

/**
 * Non-static-mock test class. Non-static-mock test class and static-mock class have to be separated.
 *
 * @author Zhahui
 *
 */
class SessionFilterTest {
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
    void init_ShouldThrowException_WhenFilterConfigIsNull() {
        assertThatThrownBy(() -> {
            filterToTest.init(null);
        }).isInstanceOf(FilterException.class).hasCauseExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("FilterConfig is null");
    }

}

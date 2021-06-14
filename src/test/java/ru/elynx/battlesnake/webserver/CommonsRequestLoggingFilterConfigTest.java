package ru.elynx.battlesnake.webserver;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Tag("Internals")
class CommonsRequestLoggingFilterConfigTest {
    @Test
    void test_log_filter() {
        CommonsRequestLoggingFilterConfig tested = new CommonsRequestLoggingFilterConfig();

        assertDoesNotThrow(() -> {
            CommonsRequestLoggingFilter result = tested.logFilter();
            assertNotNull(result);
        });
    }
}

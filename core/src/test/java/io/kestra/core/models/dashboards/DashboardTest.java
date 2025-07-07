package io.kestra.core.models.dashboards;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.validations.ModelValidator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@KestraTest
class DashboardTest {
    @Inject
    private ModelValidator modelValidator;

    @Test
    void validDashboard() {
        final Dashboard dashboard = Dashboard.builder()
            .id("foo")
            .title("FOO")
            .tenantId("tenant1")
            .build();

        assertThat(modelValidator.isValid(dashboard)).isNotPresent();
    }

    @Test
    void invalidDashboard() {
        final Dashboard dashboard = Dashboard.builder()
            .id("foo")
            .title("")
            .tenantId("tenant1")
            .build();

        assertThat(modelValidator.isValid(dashboard)).hasValueSatisfying(error ->
            assertThat(error.getMessage()).startsWith("title: Title must be at 1-250 characters")
        );
    }
}

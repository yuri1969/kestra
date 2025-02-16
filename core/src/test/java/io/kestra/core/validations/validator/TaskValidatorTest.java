package io.kestra.core.validations.validator;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.models.tasks.TaskInterface;
import io.kestra.core.models.validations.ModelValidator;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@KestraTest
class TaskValidatorTest {
    @Inject
    private ModelValidator modelValidator;

    @Test
    void isValid() {
        final TaskInterface task = new ValidTask();
        final Optional<ConstraintViolationException> validate = modelValidator.isValid(task);

        assertThat(validate.isPresent(), is(false));
    }

    @Test
    void isValidNotMatchingTaskName() {
        final TaskInterface task = new NotMatchingNameTask();
        final Optional<ConstraintViolationException> validate = modelValidator.isValid(task);

        assertThat(validate.isPresent(), is(true));
        assertThat(validate.get().getMessage(), containsString("Type doesn't match the task class. Please, check letter case."));
    }

    private static class ValidTask implements TaskInterface {
        @Override
        public String getId() {
            return "testValid";
        }

        @Override
        public String getType() {
            return this.getClass().getCanonicalName();
        }
    }

    private static class NotMatchingNameTask implements TaskInterface {
        @Override
        public String getId() {
            return "testInvalid";
        }

        @Override
        public String getType() {
            return "foo.bar.Baz";
        }
    }
}
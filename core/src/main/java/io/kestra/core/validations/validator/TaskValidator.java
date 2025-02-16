package io.kestra.core.validations.validator;

import io.kestra.core.models.tasks.TaskInterface;
import io.kestra.core.validations.TaskValidation;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;

@Singleton
@Introspected
public class TaskValidator implements ConstraintValidator<TaskValidation, TaskInterface> {
    @Override
    public boolean isValid(@Nullable TaskInterface value, @NonNull AnnotationValue<TaskValidation> annotationMetadata, @NonNull ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        if (!typeExactlyMatchesClass(value)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Type doesn't match the task class. Please, check letter case.")
                .addConstraintViolation();
            return false;
        }

        return true;
    }

    private static boolean typeExactlyMatchesClass(TaskInterface value) {
        return value.getType().equals(value.getClass().getCanonicalName());
    }
}

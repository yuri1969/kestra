package io.kestra.core.validations;

import io.kestra.core.validations.validator.TaskValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TaskValidator.class)
public @interface TaskValidation {
    String message() default "invalid task";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

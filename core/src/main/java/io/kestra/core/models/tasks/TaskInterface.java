package io.kestra.core.models.tasks;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.kestra.core.models.Plugin;
import io.kestra.core.validations.TaskValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@TaskValidation
public interface TaskInterface extends Plugin {
    @NotNull
    @NotBlank
    @Pattern(regexp="^[a-zA-Z0-9][a-zA-Z0-9_-]*")
    String getId();

    @Override
    @NotNull
    @NotBlank
    String getType();
}

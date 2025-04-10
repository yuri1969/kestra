package io.kestra.cli.commands;

import io.kestra.cli.AbstractApiCommand;
import io.kestra.cli.AbstractValidateCommand;
import io.kestra.webserver.responses.BulkResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.client.netty.DefaultHttpClient;

import java.net.URISyntaxException;

public abstract class AbstractTriggersCommand extends AbstractApiCommand {

    /**
     * Enables/disables all {@link io.kestra.core.models.triggers.Trigger} at once.
     *
     * @param disabled the desired state of triggers.
     * @return 0 if success, 1 otherwise.
     * @throws URISyntaxException when an HTTP connection fails.
     */
    protected int changeTriggersDisabledState(boolean disabled) throws URISyntaxException {
        try(DefaultHttpClient client = client()) {
            MutableHttpRequest<String> request = HttpRequest
                .POST(apiUri("/triggers/set-disabled/by-query?disabled=" + (disabled ? "true" : "false")), null);
            BulkResponse updated = client.toBlocking().retrieve(
                this.requestOptions(request),
                BulkResponse.class
            );

            stdOut("All %d triggers successfully %s".formatted(updated.getCount(), (disabled ? "disabled" : "enabled")));
        } catch (HttpClientResponseException e){
            AbstractValidateCommand.handleHttpException(e, "trigger");
            return 1;
        }

        return 0;
    }
}
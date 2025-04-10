package io.kestra.cli.commands.sys.triggers;

import io.kestra.core.models.triggers.Trigger;
import io.kestra.core.utils.IdUtils;
import io.kestra.jdbc.repository.AbstractJdbcTriggerRepository;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class EnableTriggersCommandTest {

    @Test
    void call() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        Trigger triggerDisabled = Trigger.builder()
            .flowId(IdUtils.create())
            .namespace("io.kestra.unittest")
            .triggerId(IdUtils.create())
            .disabled(true)
            .build();

        Trigger triggerNotDisabled = Trigger.builder()
            .flowId(IdUtils.create())
            .namespace("io.kestra.unittest")
            .triggerId(IdUtils.create())
            .build();

        try (ApplicationContext ctx = ApplicationContext.run(Environment.CLI, Environment.TEST)) {
            EmbeddedServer embeddedServer = ctx.getBean(EmbeddedServer.class);
            embeddedServer.start();

            AbstractJdbcTriggerRepository jdbcTriggerRepository = ctx.getBean(AbstractJdbcTriggerRepository.class);

            jdbcTriggerRepository.save(triggerDisabled);
            jdbcTriggerRepository.save(triggerNotDisabled);

            String[] args = {
                "--server",
                embeddedServer.getURL().toString(),
                "--user",
                "myuser:pass:word"
            };
            Integer call = PicocliRunner.call(EnableTriggersCommand.class, ctx, args);

            assertThat(call).isZero();
            assertThat(out.toString()).contains("All 2 triggers successfully enabled");

            assertThat(jdbcTriggerRepository.findLast(triggerDisabled)).map(Trigger::getDisabled).hasValue(false);
            assertThat(jdbcTriggerRepository.findLast(triggerNotDisabled)).map(Trigger::getDisabled).hasValue(false);
        }
    }
}
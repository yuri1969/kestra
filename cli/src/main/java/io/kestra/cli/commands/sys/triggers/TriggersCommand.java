package io.kestra.cli.commands.sys.triggers;

import io.kestra.cli.AbstractCommand;
import io.kestra.cli.App;
import io.micronaut.configuration.picocli.PicocliRunner;
import lombok.SneakyThrows;
import picocli.CommandLine;

@CommandLine.Command(
    name = "triggers",
    description = "Manage Kestra triggers",
    mixinStandardHelpOptions = true,
    subcommands = {
        DisableTriggersCommand.class,
        EnableTriggersCommand.class
    }
)
public class TriggersCommand extends AbstractCommand {
    @SneakyThrows
    @Override
    public Integer call() throws Exception {
        super.call();

        PicocliRunner.call(App.class, "sys", "triggers", "--help");

        return 0;
    }
}
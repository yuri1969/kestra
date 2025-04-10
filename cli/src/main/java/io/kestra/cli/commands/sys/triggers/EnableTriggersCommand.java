package io.kestra.cli.commands.sys.triggers;

import io.kestra.cli.commands.AbstractTriggersCommand;
import picocli.CommandLine;

@CommandLine.Command(
    name = "enable",
    description = "Enable all Kestra triggers.",
    mixinStandardHelpOptions = true
)
public class EnableTriggersCommand extends AbstractTriggersCommand {
    @Override
    public Integer call() throws Exception {
        super.call();
        return super.changeTriggersDisabledState(false);
    }
}
package net.slipcor.sponge.bowsplus.cmds;

import net.slipcor.sponge.bowsplus.BowsPlus;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.List;

public class BowsPlusMain implements CommandExecutor {
    public BowsPlusMain(final BowsPlus plugin, final List<SubCommand> subCommands) {
        CommandSpec.Builder builder = CommandSpec.builder().description(Text.of("Base command for BowsPlus."));
        for (final SubCommand cmd : subCommands) {
            builder = builder.child(cmd.cs, cmd.labels);
        }
        final CommandSpec spec = builder.build();
        Sponge.getCommandManager().register(plugin, spec, "bowsplus", "bp", "bow");
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        src.sendMessage(Text.of("/bowsplus reload - reload the configuration"));
        src.sendMessage(Text.of("/bowsplus [type] [subvalue] - set your bow to spawn said type of thing"));
        return CommandResult.success();
    }
}

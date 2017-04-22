package net.slipcor.sponge.bowsplus.cmds;

import net.slipcor.sponge.bowsplus.BowsPlus;
import net.slipcor.sponge.bowsplus.utils.Perms;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.List;

public abstract class SubCommand implements CommandExecutor {
    final CommandSpec cs;
    final String[] labels;
    protected SubCommand(final BowsPlus plugin, final List<CommandElement> args, final Perms perm, final String description, final String... label) {
        cs = CommandSpec.builder()
                .description(Text.of(description))
                .arguments(args.toArray(new CommandElement[0]))
                .permission(perm.toString())
                .executor(this)
                .build();
        labels = label;
        Sponge.getCommandManager().register(plugin, cs, label[0]); // only hook the first label as main command!
    }
}

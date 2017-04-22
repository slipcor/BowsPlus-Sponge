package net.slipcor.sponge.bowsplus.cmds.user;

import net.slipcor.sponge.bowsplus.BowsPlus;
import net.slipcor.sponge.bowsplus.cmds.SubCommand;
import net.slipcor.sponge.bowsplus.utils.Language;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Arrays;

public class CmdReset extends SubCommand {
    final BowsPlus plugin;
    public CmdReset(final BowsPlus plugin) {
        super(plugin, Arrays.asList(
                GenericArguments.none()
                ), "Will allow to reset your bow.", "reset");
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Language.ERROR_COMMAND_ONLY_PLAYERS.red());
            return CommandResult.empty();
        }
        Player player = (Player) src;
        plugin.removeMeta(player);
        player.sendMessage(Language.GOOD_SET_RESET.green());

        return CommandResult.success();
    }
}

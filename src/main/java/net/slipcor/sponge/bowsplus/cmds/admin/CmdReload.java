package net.slipcor.sponge.bowsplus.cmds.admin;

import net.slipcor.sponge.bowsplus.BowsPlus;
import net.slipcor.sponge.bowsplus.cmds.SubCommand;
import net.slipcor.sponge.bowsplus.utils.Language;
import net.slipcor.sponge.bowsplus.utils.Perms;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;

import java.util.Collections;

public class CmdReload extends SubCommand {
    final BowsPlus plugin;
    public CmdReload(final BowsPlus plugin) {
        super(plugin, Collections.singletonList(GenericArguments.none()), Perms.CMD_RELOAD, "Reloads the BowsPlus config.", "bowsplusreload", "reload", "r");
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        if (plugin.loadConfig() && plugin.loadLanguage()) {
            src.sendMessage(Language.GOOD_RELOAD_SUCCESS.green());
        } else {
            src.sendMessage(Language.ERROR_CONFIG_LOAD.red());
        }
        return CommandResult.success();
    }
}

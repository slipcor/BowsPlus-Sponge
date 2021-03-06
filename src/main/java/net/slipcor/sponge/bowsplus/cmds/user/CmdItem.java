package net.slipcor.sponge.bowsplus.cmds.user;

import com.flowpowered.math.vector.Vector3d;
import net.slipcor.sponge.bowsplus.BowsPlus;
import net.slipcor.sponge.bowsplus.cmds.SubCommand;
import net.slipcor.sponge.bowsplus.utils.*;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Optional;

public class CmdItem extends SubCommand implements Callable {
    final BowsPlus plugin;
    public CmdItem(final BowsPlus plugin) {
        super(plugin, Arrays.asList(
                GenericArguments.catalogedElement(Text.of("material"), ItemType.class),
                GenericArguments.optional(
                        GenericArguments.string(Text.of("subtype"))
                )
                ), Perms.TYPE_ITEM, "Will allow to shoot an item.", "item");
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Language.ERROR_COMMAND_ONLY_PLAYERS.red());
            return CommandResult.empty();
        }
        final Player player = (Player) src;

        Optional<ItemType> oType = args.getOne("material");

        if (oType.isPresent()) {
            if (Config.getBoolean(Config.EXPLICIT)) {
                String permission = Perms.TYPE_ITEM.toString()+"."+oType.get().getName().toLowerCase();
                if (!player.hasPermission(permission)) {
                    player.sendMessage(Language.BAD_PERMISSION.red(permission));
                    return CommandResult.empty();
                }
            }
            plugin.applyMeta(player, "item", this, oType.get());
            player.sendMessage(Language.GOOD_SET_BOW.green(Language.TYPE_ITEM.toString(), oType.get().getName()));
        } else {
            player.sendMessage(Language.BAD_TYPE_NOT_FOUND.red(Language.TYPE_ITEM.toString(), oType.toString()));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    @Override
    public void attempt(Player player, Arrow arrow, String key, Object value) {
        if (value instanceof ItemType) {
            ItemType type = (ItemType) value;

            ItemStack itemStack = ItemStack.of(type, 1);

            Vector3d position = arrow.getLocation().getPosition();
            Vector3d velocity = arrow.get(Keys.VELOCITY).orElse(new Vector3d(player.getRotation()));
            arrow.remove();

            final Entity item = player.getWorld().createEntity(EntityTypes.ITEM, position);
            item.offer(Keys.REPRESENTED_ITEM, itemStack.createSnapshot());
            item.offer(Keys.VELOCITY, velocity);
            item.offer(Keys.DISPLAY_NAME, Text.of("Bows#Plus#Projectile#"+System.currentTimeMillis()));
            player.getWorld().spawnEntity(item, Cause.source(EntitySpawnCause.builder()
                    .entity(item).type(SpawnTypes.PLUGIN).build()).build());
        }
    }
}

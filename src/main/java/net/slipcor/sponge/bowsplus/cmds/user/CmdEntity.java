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
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Optional;

public class CmdEntity extends SubCommand implements Callable {
    final BowsPlus plugin;
    public CmdEntity(final BowsPlus plugin) {
        super(plugin, Arrays.asList(
                GenericArguments.catalogedElement(Text.of("entity"), EntityType.class),
                GenericArguments.optional(
                        GenericArguments.string(Text.of("subtype"))
                )
                ), Perms.TYPE_ENTITY, "Will allow to shoot an entity.", "entity");
        this.plugin = plugin;
    }
    @Override
    public CommandResult execute(final CommandSource src, final CommandContext args) throws CommandException {
        if (!(src instanceof Player)) {
            src.sendMessage(Language.ERROR_COMMAND_ONLY_PLAYERS.red());
            return CommandResult.empty();
        }
        final Player player = (Player) src;

        Optional<EntityType> oType = args.getOne("entity");

        if (oType.isPresent()) {
            if (Config.getBoolean(Config.EXPLICIT)) {
                String permission = Perms.TYPE_ENTITY.toString()+"."+oType.get().getName().toLowerCase();
                if (!player.hasPermission(permission)) {
                    player.sendMessage(Language.BAD_PERMISSION.red(permission));
                    return CommandResult.empty();
                }
            }

            plugin.applyMeta(player, "entity", this, oType.get());
            player.sendMessage(Language.GOOD_SET_BOW.green(Language.TYPE_ENTITY.toString(), oType.get().getName()));
        } else {
            player.sendMessage(Language.BAD_TYPE_NOT_FOUND.red(Language.TYPE_ENTITY.toString(), oType.toString()));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    @Override
    public void attempt(Player player, Arrow arrow, String key, Object value) {
        //TODO: check player for required material

        if (value instanceof EntityType) {
            EntityType type = (EntityType) value;

            Vector3d position = arrow.getLocation().getPosition();
            Vector3d velocity = arrow.get(Keys.VELOCITY).orElse(new Vector3d(player.getRotation()));
            arrow.remove();

            if (Projectile.class.isAssignableFrom(type.getEntityClass())) { // isAssignableFrom(Projectile.class) did not work :P // .getName().contains(".projectile.")
                player.launchProjectile((Class<Projectile>)type.getEntityClass(), velocity);
            } else {
                final Entity item = player.getWorld().createEntity(type, position);
                item.offer(Keys.VELOCITY, velocity);
                player.getWorld().spawnEntity(item, Cause.source(EntitySpawnCause.builder()
                        .entity(item).type(SpawnTypes.PLUGIN).build()).build());
            }
        }
    }
}

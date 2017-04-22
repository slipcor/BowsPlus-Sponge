package net.slipcor.sponge.bowsplus.cmds.user;

import com.flowpowered.math.vector.Vector3d;
import net.slipcor.sponge.bowsplus.BowsPlus;
import net.slipcor.sponge.bowsplus.cmds.SubCommand;
import net.slipcor.sponge.bowsplus.utils.Callable;
import net.slipcor.sponge.bowsplus.utils.Config;
import net.slipcor.sponge.bowsplus.utils.Language;
import net.slipcor.sponge.bowsplus.utils.Perms;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
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
                GenericArguments.string(Text.of("entity")),
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

        Optional<String> oEntity = args.getOne("entity");
        String entity = oEntity.orElse("sheep");

        Optional<EntityType> oType = Sponge.getRegistry().getType(EntityType.class, entity);
        if (oType.isPresent()) {
            if (Config.getBoolean(Config.EXPLICIT)) {
                String permission = Perms.TYPE_ENTITY.toString()+"."+oType.get().getName().toLowerCase();
                if (!player.hasPermission(permission)) {
                    player.sendMessage(Language.BAD_PERMISSION.red(permission));
                    return CommandResult.empty();
                }
            }

            plugin.applyMeta(player, "entity", this, entity);
            player.sendMessage(Language.GOOD_SET_BOW.green(Language.TYPE_ENTITY.toString(), entity));
        } else {
            player.sendMessage(Language.BAD_TYPE_NOT_FOUND.red(Language.TYPE_ENTITY.toString(), entity));
            return CommandResult.empty();
        }

        return CommandResult.success();
    }

    @Override
    public void attempt(Player player, Arrow arrow, String key, String value) {
        //TODO: check player for required material
        Optional<EntityType> oType = Sponge.getRegistry().getType(EntityType.class, value);
        if (oType.isPresent()) {
            EntityType type = oType.get();

            Vector3d position = arrow.getLocation().getPosition();
            Vector3d velocity = arrow.get(Keys.VELOCITY).orElse(new Vector3d(player.getRotation()));
            arrow.remove();

            final Entity item = player.getWorld().createEntity(type, position);
            item.offer(Keys.VELOCITY, velocity);
            player.getWorld().spawnEntity(item, Cause.source(EntitySpawnCause.builder()
                    .entity(item).type(SpawnTypes.PLUGIN).build()).build());
        }

    }
}

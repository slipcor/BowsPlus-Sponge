package net.slipcor.sponge.bowsplus;

import com.google.inject.Inject;
import net.slipcor.sponge.bowsplus.cmds.BowsPlusMain;
import net.slipcor.sponge.bowsplus.cmds.SubCommand;
import net.slipcor.sponge.bowsplus.cmds.admin.CmdReload;
import net.slipcor.sponge.bowsplus.cmds.user.CmdEntity;
import net.slipcor.sponge.bowsplus.cmds.user.CmdItem;
import net.slipcor.sponge.bowsplus.cmds.user.CmdReset;
import net.slipcor.sponge.bowsplus.utils.*;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.arrow.Arrow;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(id = "bowsplus", name = "BowsPlus", version = "0.1")
public class BowsPlus {
    final Map<String, CallContainer> callMap = new HashMap<>();
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path configDir;

    @Inject
    private PluginContainer container;

    @Inject
    public BowsPlus(Logger logger) {
        this.logger = logger;
    }

    public void applyMeta(Player player, String key, Callable cmd, Object value) {
        callMap.put(player.getName(), new CallContainer(key, cmd, value));
    }

    public boolean loadConfig() {
        try {
            Config.init(configDir);
        } catch (IOException e) {
            System.out.print(e);
            return false;
        }
        return true;
    }

    public boolean loadLanguage() {
        try {
            Language.init(configDir.resolveSibling(Config.getString(Config.LANGUAGE_FILE)));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void removeMeta(Player player) {
        callMap.remove(player.getName());
    }

    @Listener
    public void onPreInit(final GameInitializationEvent event){
        if (!loadConfig() || !loadLanguage()) {
            Sponge.getServer().getConsole().sendMessage(Language.ERROR_CONFIG_LOAD.red());
        }
    }

    @Listener
    public void onServerStart(final GameStartedServerEvent event) {
        // initiate child commands
        List<SubCommand> subCommands = new ArrayList<>();
        subCommands.add(new CmdReload(this));

        subCommands.add(new CmdEntity(this));
        subCommands.add(new CmdItem(this));

        subCommands.add(new CmdReset(this));

        // initiate main command
        new BowsPlusMain(this, subCommands);
        final Tracker trackMe = new Tracker(this);
    }



    @Listener
    public void onProjectileLaunch(final SpawnEntityEvent event) {
        Cause cause = event.getCause();
        Optional<Player> shooter = cause.first(Player.class);

        Optional<Entity> entity = event.getEntities().stream().filter(e -> e instanceof Projectile).findFirst();

        if (shooter.isPresent() && entity.isPresent()) {
            Player player = shooter.get();
            Projectile projectile = (Projectile) entity.get();
            if (!callMap.containsKey(player.getName())) {
                return;
            }
            CallContainer container = callMap.get(player.getName());
            container.callFor(player, (Arrow) projectile);
            event.setCancelled(true);
        }
    }

    @Listener
    public void onPickup(final ChangeInventoryEvent.Pickup event) {
        Item item = event.getTargetEntity();
        if (item.supports(Keys.DISPLAY_NAME)) {
            Text displayName = item.get(Keys.DISPLAY_NAME).orElse(Text.EMPTY);
            String prefix = "Bows#Plus#Projectile#";
            if (displayName.toPlain().startsWith(prefix)) {
                String content = displayName.toPlain().substring(prefix.length());
                try {
                    Long l = Long.parseLong(content);
                    if (System.currentTimeMillis()-l < 1000) {
                        event.setCancelled(true);
                    } else {
                        item.remove(Keys.DISPLAY_NAME);
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }
}

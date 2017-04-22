package net.slipcor.sponge.bowsplus.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;

public class CallContainer {
    String key;
    Callable cmd;
    Object value;
    public CallContainer(String key, Callable cmd, Object value) {
        this.key = key;
        this.cmd = cmd;
        this.value = value;
    }

    public void callFor(Player player, Arrow arrow) {
        cmd.attempt(player, arrow, key, value);
    }
}

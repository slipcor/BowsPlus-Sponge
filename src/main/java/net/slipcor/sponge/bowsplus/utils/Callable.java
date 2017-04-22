package net.slipcor.sponge.bowsplus.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.arrow.Arrow;

public interface Callable {
    void attempt(Player player, Arrow arrow, String key, String value);
}

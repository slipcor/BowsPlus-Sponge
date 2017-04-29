package net.slipcor.sponge.bowsplus.utils;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public enum Language {

    BAD_PERMISSION("bad.permission", "You do not have the permission %0%"),
    BAD_TYPE_NOT_FOUND("bad.type_not_found", "%0% type not found: %1%"),

    ERROR_COMMAND_ONLY_PLAYERS("error.cmc_only_players", "This is a player only command!"),
    ERROR_CONFIG_LOAD("error.config_load", "Error while loading config! Plugin will not work properly!"),
    ERROR_ENTITY_NOT_SUPPORTED("error.entity_not_supported", "Entity '%0%' currently not supported: %1%"),

    WARN_INCOMPLETE_ENTITY("warn.incomplete_entity", "Support for this entity is currently not completed: %0%"),

    GOOD_SET_BOW("good.set_bow", "Your bow will spawn the %0% %1%."),
    GOOD_SET_RESET("good.set_reset", "Your bow will shoot arrows again!"),

    TYPE_ENTITY("type.entity", "Entity"),
    TYPE_ITEM("type.item", "Item"),

    GOOD_RELOAD_SUCCESS("good.reload_success", "Configs reloaded!");

    String nodes;
    String msg;

    static ConfigurationLoader<CommentedConfigurationNode> loader = null;
    static CommentedConfigurationNode rootNode = null;

    Language(final String nodes, final String msg) {
        this.nodes = nodes;
        this.msg = msg;
    }

    public static void init(final Path path) throws IOException {
        File file = path.toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
        loader = HoconConfigurationLoader.builder().setPath(path).setDefaultOptions(ConfigurationOptions.defaults().setShouldCopyDefaults(true)).build();
        rootNode = loader.load();

        boolean changed = false;

        for (Language l : Language.values()) {
            CommentedConfigurationNode node = rootNode.getNode((Object[])l.nodes.split("\\."));
            if (node.isVirtual() && !changed) {
                changed = true;
            }
            l.msg = rootNode.getNode((Object[])l.nodes.split("\\.")).getString(l.msg);
        }
        if (changed) {
            loader.save(rootNode);
        }
    }

    @Override
    public String toString() {
        return this.msg;
    }

    public Text green() {
        return Text.builder(this.msg).color(TextColors.GREEN).build();
    }

    public Text red() {
        return Text.builder(this.msg).color(TextColors.RED).build();
    }

    public Text yellow() {
        return Text.builder(this.msg).color(TextColors.YELLOW).build();
    }

    public Text green(final String... args) {
        return Text.builder(replace(this.msg, args)).color(TextColors.GREEN).build();
    }

    public Text red(final String... args) {
        return Text.builder(replace(this.msg, args)).color(TextColors.RED).build();
    }

    public Text yellow(final String... args) {
        return Text.builder(replace(this.msg, args)).color(TextColors.YELLOW).build();
    }

    private String replace(String content, final String... args) {
        for(int i=0;i<args.length;i++) {
            content = content.replace("%"+i+"%", args[i]);
        }
        return content;
    }
}

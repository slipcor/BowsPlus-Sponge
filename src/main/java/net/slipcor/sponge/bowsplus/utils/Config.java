package net.slipcor.sponge.bowsplus.utils;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public enum Config {
    //C_GENERAL("general", null, "=== [ General Settings ] ==="),

    //GENERAL("general.require.source", false, "Require a player to have a fitting item or a fitting spawn egg"),

    C_PERMS("perms", null, "=== [ Permission Settings ] ==="),
    EXPLICIT("perms.require.explicit", false, "Require a player to have the exact item / entity permission"),

    C_PLUGIN("plugin", null, "=== [ Plugin Settings ] ==="),

    CALL_HOME("plugin.callhome", true, "This activates phoning home to www.slipcor.net"),
    LANGUAGE_FILE("plugin.language_file", "lang_en.conf", "This is the language file you wish to use");

    String nodes;
    Object value;
    String comment;

    Config(final String nodes, final Object value, final String comment) {
        this.nodes = nodes;
        this.value = value;
        this.comment = comment;
    }

    static ConfigurationLoader<CommentedConfigurationNode> loader = null;
    static CommentedConfigurationNode rootNode = null;

    public static void init(final Path path) throws IOException {
        loader = HoconConfigurationLoader.builder().setPath(path).setDefaultOptions(ConfigurationOptions.defaults().setShouldCopyDefaults(true)).build();
        rootNode = loader.load();
        boolean changed = false;

        for (Config c : Config.values()) {
            final CommentedConfigurationNode node = rootNode.getNode((Object[])c.nodes.split("\\."));
            if (node.isVirtual()) {
                changed = true;
                node.setComment(c.comment);
            }
            if (c.value != null) {
                c.value = node.getValue(c.value);
            }
        }
        if (changed) {
            loader.save(rootNode);
        }
    }

    public static Boolean getBoolean(Config cfg) {
        return (Boolean) cfg.value;
    }

    public static Integer getInt(Config cfg) {
        return (Integer) cfg.value;
    }

    public static Float getFloat(Config cfg) {
        return rootNode.getNode((Object[])cfg.nodes.split("\\.")).getFloat();
    }

    public static List<String> getList(Config cfg) {
        return (List<String>) cfg.value;
    }

    public static String getString(Config cfg) {
        return (String) cfg.value;
    }
}

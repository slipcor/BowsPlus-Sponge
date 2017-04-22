package net.slipcor.sponge.bowsplus.utils;

public enum Perms {
    CMD_RELOAD("bowsplus.cmd.reload","Allows use of the reload command"),

    TYPE_ENTITY("bowsplus.type.entity","Allows shooting of an entity"),

    TYPE_ITEM("bowsplus.type.item","Allows shooting of an item");

    final String node;
    final String description;

    Perms(final String node, final String desc) {
        this.node = node;
        this.description = desc;
    }

    @Override
    public String toString() {
        return node;
    }
}

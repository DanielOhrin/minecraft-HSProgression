package net.highskiesmc.hsprogression.api;

public class Permission extends net.milkbowl.vault.permission.Permission {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return false;
    }

    @Override
    public boolean playerHas(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerAdd(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerRemove(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupHas(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupAdd(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean groupRemove(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerInGroup(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerAddGroup(String s, String s1, String s2) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String s, String s1, String s2) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String s, String s1) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(String s, String s1) {
        return null;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}

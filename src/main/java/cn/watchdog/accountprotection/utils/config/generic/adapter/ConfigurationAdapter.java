package cn.watchdog.accountprotection.utils.config.generic.adapter;

import net.md_5.bungee.api.plugin.*;

import java.util.*;

public interface ConfigurationAdapter {
    Plugin getPlugin();

    void reload();

    String getString(String path, String def);

    double getDouble(String path, double def);

    int getInteger(String path, int def);

    boolean getBoolean(String path, boolean def);

    long getLong(String path, long def);

    List<String> getStringList(String path, List<String> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);
}

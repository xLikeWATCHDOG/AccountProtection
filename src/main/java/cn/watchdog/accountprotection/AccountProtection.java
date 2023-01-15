package cn.watchdog.accountprotection;

import cn.watchdog.accountprotection.listeners.*;
import cn.watchdog.accountprotection.utils.config.*;
import cn.watchdog.accountprotection.utils.config.generic.*;
import cn.watchdog.accountprotection.utils.storage.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.*;
import net.md_5.bungee.api.plugin.*;

import java.io.*;
import java.nio.file.*;

public final class AccountProtection extends Plugin {
    public static AccountProtection instance;
    public static SqlStorage sqlStorage;
    public KeyedConfiguration config;

    public static AccountProtection getInstance() {
        return instance;
    }

    public static SqlStorage getSqlStorage() {
        return sqlStorage;
    }

    @Override
    public void onEnable() {
        instance = this;
        config = new ConfigYaml(this, new BungeeConfigAdapter(this, resolveConfig("config.yml").toFile()));
        SqlUtils.initSql();
        try {
            sqlStorage.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getProxy().getPluginManager().registerListener(this, new PlayerListeners());//注册监听器
    }

    @Override
    public void onDisable() {
        sqlStorage.shutdown();
    }

    private Path resolveConfig(String fileName) {
        Path configFile = getDataFolder().toPath().toAbsolutePath().resolve(fileName);

        // 如果配置不存在，则根据resources目录中的模板创建它
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e) {
                // ignore
            }

            try (InputStream is = getResourceAsStream(fileName)) {
                Files.copy(is, configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }
}

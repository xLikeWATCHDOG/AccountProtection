package cn.watchdog.accountprotection.utils.config;

import cn.watchdog.accountprotection.utils.config.generic.*;
import cn.watchdog.accountprotection.utils.config.generic.key.*;
import cn.watchdog.accountprotection.utils.storage.misc.*;
import com.google.common.collect.*;

import java.util.*;

import static cn.watchdog.accountprotection.utils.config.generic.key.ConfigKeyFactory.*;

public class ConfigKeys {

    /**
     * 数据库设置，用户名，密码等，供任何数据库使用
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES = notReloadable(key(c -> {
        int maxPoolSize = c.getInteger("data.pool-settings.maximum-pool-size", c.getInteger("data.pool-size", 10));
        int minIdle = c.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = c.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int keepAliveTime = c.getInteger("data.pool-settings.keepalive-time", 0);
        int connectionTimeout = c.getInteger("data.pool-settings.connection-timeout", 5000);
        Map<String, String> props = ImmutableMap.copyOf(c.getStringMap("data.pool-settings.properties", ImmutableMap.of()));

        return new StorageCredentials(c.getString("data.address", null), c.getString("data.database", null), c.getString("data.username", null), c.getString("data.password", null), maxPoolSize, minIdle, maxLifetime, keepAliveTime, connectionTimeout, props);
    }));

    /**
     * 所有SQL表的前缀
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX = notReloadable(key(c -> c.getString("data.table-prefix", c.getString("data.table_prefix", "skywars_"))));


    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

package cn.watchdog.accountprotection.utils.storage;

import cn.watchdog.accountprotection.*;
import cn.watchdog.accountprotection.utils.config.*;
import cn.watchdog.accountprotection.utils.config.generic.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.connection.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.connection.hikari.*;

public class SqlUtils {
    public static void initSql() {
        KeyedConfiguration config = AccountProtection.getInstance().config;
        ConnectionFactory connectionFactory = new PostgreConnectionFactory(config.get(ConfigKeys.DATABASE_VALUES));
        AccountProtection.sqlStorage = new SqlStorage(connectionFactory, config.get(ConfigKeys.SQL_TABLE_PREFIX));
    }
}

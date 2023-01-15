package cn.watchdog.accountprotection.utils.storage.implementation.sql.connection.hikari;

import cn.watchdog.accountprotection.utils.storage.implementation.sql.connection.*;
import cn.watchdog.accountprotection.utils.storage.misc.*;
import com.zaxxer.hikari.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 抽象 {@link ConnectionFactory} 使用 {@link com.zaxxer.hikari.HikariDataSource}.
 */
public abstract class HikariConnectionFactory implements ConnectionFactory {
    private final StorageCredentials configuration;
    private HikariDataSource hikari;

    public HikariConnectionFactory(StorageCredentials configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取数据库使用的默认端口
     *
     * @return 默认端口
     */
    protected abstract String defaultPort();

    /**
     * 配置 {@link com.zaxxer.hikari.HikariConfig} 使用相关的数据库属性。
     *
     * <p>每个驱动程序的做法略有不同。.</p>
     *
     * @param config       the hikari config
     * @param address      the database address
     * @param port         the database port
     * @param databaseName the database name
     * @param username     the database username
     * @param password     the database password
     */
    protected abstract void configureDatabase(HikariConfig config, String address, String port, String databaseName, String username, String password);

    /**
     * 允许连接工厂实例在设置某些属性之前覆盖它们。
     *
     * @param properties 当前的属性
     */
    protected void overrideProperties(Map<String, String> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    /**
     * 将给定的连接属性设置到配置。
     *
     * @param config     the hikari config
     * @param properties the properties
     */
    protected void setProperties(HikariConfig config, Map<String, String> properties) {
        for (Map.Entry<String, String> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }

    /**
     * Called after the Hikari pool has been initialised
     */
    protected void postInitialize() {

    }

    @Override
    public void init(String poolName) {
        HikariConfig config = new HikariConfig();

        // 设置池名，以便日志输出可以链接回我们
        config.setPoolName(poolName);

        // 从配置文件中获取数据库信息凭证
        String[] addressSplit = this.configuration.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : defaultPort();

        // 允许实现用这些值适当地配置HikariConfig
        try {
            configureDatabase(config, address, port, this.configuration.getDatabase(), this.configuration.getUsername(), this.configuration.getPassword());
        } catch (NoSuchMethodError ignored) {
        }

        // 从配置中获取额外的连接属性
        Map<String, String> properties = new HashMap<>(this.configuration.getProperties());

        // 允许实现覆盖更改这些属性
        overrideProperties(properties);

        // 设置属性
        setProperties(config, properties);

        // 配置连接池
        config.setMaximumPoolSize(this.configuration.getMaxPoolSize());
        config.setMinimumIdle(this.configuration.getMinIdleConnections());
        config.setMaxLifetime(this.configuration.getMaxLifetime());
        config.setKeepaliveTime(this.configuration.getKeepAliveTime());
        config.setConnectionTimeout(this.configuration.getConnectionTimeout());

        // 不执行任何初始连接验证—随后调用getConnection
        // 无论如何设置模式
        config.setInitializationFailTimeout(-1);

        this.hikari = new HikariDataSource(config);

        postInitialize();
    }

    @Override
    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("无法从池中获得连接。(hikari is null)");
        }
        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("无法从池中获得连接。(getConnection returned null)");
        }
        return connection;
    }
}

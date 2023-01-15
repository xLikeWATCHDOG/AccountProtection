package cn.watchdog.accountprotection.utils.storage.implementation.sql.connection;

import java.sql.*;
import java.util.function.*;

public interface ConnectionFactory {

    String getImplementationName();

    void init(String poolName);

    void shutdown() throws Exception;

    Function<String, String> getStatementProcessor();

    Connection getConnection() throws SQLException;

}

package cn.watchdog.accountprotection.utils.storage.implementation.sql;

import cn.watchdog.accountprotection.*;
import cn.watchdog.accountprotection.utils.storage.implementation.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.connection.*;
import cn.watchdog.accountprotection.utils.storage.misc.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SqlStorage implements StorageImplementation {
    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;

    public SqlStorage(ConnectionFactory connectionFactory, String tablePrefix) {
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public Function<String, String> getStatementProcessor() {
        return this.statementProcessor;
    }

    @Override
    public void init() throws Exception {
        this.connectionFactory.init("account-protection-pool");
		/*
		boolean tableExists;
		Connection connection = this.connectionFactory.getConnection();
		tableExists = tableExists(connection, this.statementProcessor.apply("{prefix}player_base_info"));
		connection.close();
		*/
        applySchema();
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "cn/watchdog/accountprotection/resources/schema/" + this.connectionFactory.getImplementationName().toLowerCase() + ".sql";
        try (InputStream is = AccountProtection.getInstance().getResourceAsStream(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is).stream().map(this.statementProcessor).collect(Collectors.toList());
        }

        try (Connection connection = this.connectionFactory.getConnection()) {
            boolean utf8mb4Unsupported = false;

            try (Statement s = connection.createStatement()) {
                for (String query : statements) {
                    s.addBatch(query);
                }

                try {
                    s.executeBatch();
                } catch (BatchUpdateException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        utf8mb4Unsupported = true;
                    } else {
                        throw e;
                    }
                }
            }

            // try again
            if (utf8mb4Unsupported) {
                try (Statement s = connection.createStatement()) {
                    for (String query : statements) {
                        s.addBatch(query.replace("utf8mb4", "utf8"));
                    }

                    s.executeBatch();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            AccountProtection.getInstance().getLogger().severe("Exception whilst disabling SQL storage");
        }
    }

    public PLAYER_ACCOUNT_INFO selectPlayerAccountInfo(UUID uuid) throws SQLException {
        PLAYER_ACCOUNT_INFO player_account_info = new PLAYER_ACCOUNT_INFO(uuid);
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PLAYER_ACCOUNT_INFO.SELECT))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String secret_key = rs.getString("secret_key");
                        int zone_code = rs.getInt("zone_code");
                        String last_ip = rs.getString("last_ip");
                        int times = rs.getInt("times");
                        boolean bind = rs.getBoolean("bind");
                        player_account_info = new PLAYER_ACCOUNT_INFO(uuid, secret_key, zone_code, last_ip, times, bind);
                    } else {
                        player_account_info.setEmpty(true);
                    }
                }
            }
        }
        return player_account_info;
    }

    public void deletePlayerAccountInfo(UUID u) throws SQLException {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PLAYER_ACCOUNT_INFO.DELETE))) {
                ps.setString(1, u.toString());
                ps.execute();
            }
        }
    }

}

package cn.watchdog.accountprotection.utils.storage.misc;

import cn.watchdog.accountprotection.*;
import cn.watchdog.accountprotection.utils.*;
import cn.watchdog.accountprotection.utils.storage.implementation.sql.*;

import java.sql.*;
import java.util.*;

public class PLAYER_ACCOUNT_INFO {
    public static final String SELECT = "SELECT * FROM '{prefix}player_account_info' WHERE uuid=?";
    public static final String DELETE = "DELETE FROM '{prefix}player_account_info' WHERE uuid=?";
    private static final String UPDATE_SECRET_KEY = "UPDATE '{prefix}player_account_info' SET secret_key=? WHERE uuid=?";
    private static final String UPDATE_ZONE_CODE = "UPDATE '{prefix}player_account_info' SET zone_code=? WHERE uuid=?";
    private static final String UPDATE_LAST_IP = "UPDATE '{prefix}player_account_info' SET last_ip=? WHERE uuid=?";
    private static final String UPDATE_TIMES = "UPDATE '{prefix}player_account_info' SET times=? WHERE uuid=?";
    private static final String UPDATE_BIND = "UPDATE '{prefix}player_account_info' SET bind=? WHERE uuid=?";
    private static final String INSERT = "INSERT INTO '{prefix}player_account_info' (uuid, secret_key, zone_code, last_ip, times, bind) VALUES(?, ?, ?, ?, ?, ?)";

    private static SqlStorage sqlStorage = null;

    private final UUID uuid;
    private boolean empty = false;
    private String secret_key;
    private int zone_code;
    private String last_ip;
    private int times;
    private boolean bind;

    public PLAYER_ACCOUNT_INFO(UUID uuid) {
        this(uuid, GoogleAuthenticator.generateSecretKey(), -1, null, 1, false);
        String ip = AccountProtection.getInstance().getProxy().getPlayer(uuid).getAddress().getHostName();
        List<String> query = QueryHelper.queryIP(ip);
        this.zone_code = Integer.parseInt(query.get(6));
        this.last_ip = ip;
    }

    public PLAYER_ACCOUNT_INFO(UUID uuid, String secret_key, int zone_code, String last_ip, int times, boolean bind) {
        this.uuid = uuid;
        this.secret_key = secret_key;
        this.zone_code = zone_code;
        this.last_ip = last_ip;
        this.times = times;
        this.bind = bind;
        sqlStorage = AccountProtection.getSqlStorage();
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) throws SQLException {
        this.secret_key = secret_key;
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(UPDATE_SECRET_KEY))) {
                ps.setString(1, secret_key);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public int getZone_code() {
        return zone_code;
    }

    public void setZone_code(int zone_code) throws SQLException {
        this.zone_code = zone_code;
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(UPDATE_ZONE_CODE))) {
                ps.setInt(1, zone_code);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public String getLast_ip() {
        return last_ip;
    }

    public void setLast_ip(String last_ip) throws SQLException {
        this.last_ip = last_ip;
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(UPDATE_LAST_IP))) {
                ps.setString(1, last_ip);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) throws SQLException {
        this.times = times;
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(UPDATE_TIMES))) {
                ps.setInt(1, times);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public boolean isBind() {
        return bind;
    }

    public void setBind(boolean bind) throws SQLException {
        this.bind = bind;
        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(UPDATE_BIND))) {
                ps.setBoolean(1, bind);
                ps.setString(2, uuid.toString());
                ps.execute();
            }
        }
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public void init() throws SQLException {
        if (!empty) {
            return;
        }

        try (Connection connection = sqlStorage.getConnectionFactory().getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sqlStorage.getStatementProcessor().apply(INSERT))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, secret_key);
                ps.setInt(3, zone_code);
                ps.setString(4, last_ip);
                ps.setInt(5, times);
                ps.setBoolean(6, bind);
                ps.execute();
            }
        }
    }
}

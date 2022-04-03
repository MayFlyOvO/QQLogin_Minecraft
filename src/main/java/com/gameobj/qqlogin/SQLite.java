package com.gameobj.qqlogin;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite {
    private final Connection conn;

    public SQLite() throws SQLException {
        SQLiteConfig config = new SQLiteConfig();
        config.setSharedCache(true);
        config.enableRecursiveTriggers(true);
        SQLiteDataSource ds = new SQLiteDataSource(config);

        String url = "jdbc:sqlite:" + System.getProperty("user.dir") + "/plugins/QQLogin/database.db";
        ds.setUrl(url);

        this.conn = ds.getConnection();

        // 创建表
        String sql = "CREATE TABLE IF NOT EXISTS qq (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  uuid TEXT NOT NULL," +
                "  qq_number INTEGER NOT NULL" +
                ");";
        this.conn.createStatement().execute(sql);
    }

    // 根据uuid查询qq号
    public long getQQNumber(UUID uuid) throws SQLException {
        String sql = "SELECT qq_number FROM qq WHERE uuid = ?";
        PreparedStatement stmt = this.conn.prepareStatement(sql);
        stmt.setString(1, uuid.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getLong(1);
        }
        return 0;
    }

    // 根据qq号查询uuid
    public UUID getUUID(long qqNumber) throws SQLException {
        String sql = "SELECT uuid FROM qq WHERE qq_number = ?";
        PreparedStatement stmt = this.conn.prepareStatement(sql);
        stmt.setLong(1, qqNumber);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return UUID.fromString(rs.getString(1));
        }
        return null;
    }

    // 插入uuid和qq号
    public void insert(UUID uuid, long qqNumber) throws SQLException {
        String sql = "INSERT INTO qq (uuid, qq_number) VALUES (?, ?)";
        PreparedStatement stmt = this.conn.prepareStatement(sql);
        stmt.setString(1, uuid.toString());
        stmt.setLong(2, qqNumber);
        stmt.execute();
    }

    // 根据uuid更新qq号
    public void update(UUID uuid, long qqNumber) throws SQLException {
        String sql = "UPDATE qq SET qq_number = ? WHERE uuid = ?";
        PreparedStatement stmt = this.conn.prepareStatement(sql);
        stmt.setLong(1, qqNumber);
        stmt.setString(2, uuid.toString());
        stmt.execute();
    }

    // 关闭数据库连接
    public void close() throws SQLException {
        this.conn.close();
    }
}

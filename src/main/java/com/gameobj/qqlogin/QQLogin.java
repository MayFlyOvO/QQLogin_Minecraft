package com.gameobj.qqlogin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public final class QQLogin extends JavaPlugin {
    private static QQLogin instance = null;
    private SQLite db = null;
    private FileConfiguration config = null;
    private final Map<UUID,Captcha> unAuthedPlayers = new HashMap<>();
    @Override
    public void onEnable() {
        // 保存默认配置文件
        this.saveDefaultConfig();

        instance = this;

        // 初始化数据库
        try {
            this.db = new SQLite();
        } catch (Exception e) {
            e.printStackTrace();
            // 关闭插件
            this.getLogger().info("数据库初始化失败，插件将被关闭");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        // 注册Handler
        Bukkit.getPluginManager().registerEvents(new QQEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new ServerEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRestrictions(), this);

        // 注册命令
        this.getCommand("qqlogin").setExecutor(new CommandHandler());

        // 注册调度器
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new CaptchaBroadcastTask(), 0, 20);

        this.config = this.getConfig();
    }

    @Override
    public void onDisable() {
        // 关闭数据库
        if (this.db != null) {
            try {
                this.db.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 获取数据库
    public SQLite getDb() {
        return this.db;
    }

    // 获取配置文件
    public FileConfiguration getFileConfig() {
        return this.config;
    }

    // 获取未登录玩家列表
    public Set<UUID> getUnAuthedPlayers() {
        return this.unAuthedPlayers.keySet();
    }

    // 获取验证码
    public Captcha getCaptcha(UUID uuid) {
        return this.unAuthedPlayers.get(uuid);
    }

    // 添加玩家至未登录列表
    public void addUnAuthedPlayer(UUID player, Captcha captcha) {
        this.unAuthedPlayers.put(player,captcha);
    }

    // 判断玩家是否在未登录列表
    public boolean isUnAuthedPlayer(UUID player) {
        return this.unAuthedPlayers.containsKey(player);
    }

    // 设置新验证码
    public void setNewCaptcha(UUID player,Captcha captcha) {
        this.unAuthedPlayers.put(player,captcha);
    }

    // 从未登录列表移除玩家
    public void removeUnAuthedPlayer(UUID player) {
        this.unAuthedPlayers.remove(player);
    }

    // 获取插件实例
    public static QQLogin getInstance() {
        return instance;
    }
}

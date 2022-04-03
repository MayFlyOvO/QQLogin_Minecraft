package com.gameobj.qqlogin;

import me.dreamvoid.miraimc.api.MiraiBot;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class ServerEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        long qq = QQLogin.getInstance().getDb().getQQNumber(event.getPlayer().getUniqueId());
        Captcha captcha = null;
        if (qq != 0) {
            // 已绑定玩家，给玩家QQ发送消息
            captcha = Captcha.create(qq);
            MiraiBot bot = MiraiBot.getBot(QQLogin.getInstance().getConfig().getLong("bot-qq"));
            if (bot.getFriendList().contains(qq)) {
                bot.getFriend(qq).sendMessage("您正在尝试登录，请输入验证码！");
            }
        } else {
            event.getPlayer().sendMessage("首次使用请添加机器人QQ:" + QQLogin.getInstance().getConfig().getLong("bot-qq") + "");
            TextComponent clickMessage = new TextComponent("§4§l§n(点此一键添加)");
            clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://wpa.qq.com/msgrd?v=3&uin=" + QQLogin.getInstance().getConfig().getLong("bot-qq") + "&site=qq&menu=yes"));
            event.getPlayer().spigot().sendMessage(clickMessage);
        }
        // 添加玩家至未登录列表
        QQLogin.getInstance().addUnAuthedPlayer(event.getPlayer().getUniqueId(), captcha);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 如果玩家在未登录列表中，则删除
        if (QQLogin.getInstance().isUnAuthedPlayer(event.getPlayer().getUniqueId())) {
            QQLogin.getInstance().removeUnAuthedPlayer(event.getPlayer().getUniqueId());
        }
    }
}

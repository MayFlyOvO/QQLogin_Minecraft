package com.gameobj.qqlogin;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.bukkit.event.MiraiFriendMessageEvent;
import me.dreamvoid.miraimc.bukkit.event.MiraiNewFriendRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class QQEventListener implements Listener {
    @EventHandler
    public void onNewFriendRequest(MiraiNewFriendRequestEvent event) {
        long qq = event.getFromID();

        // 检查是否为群成员
        MiraiBot bot = MiraiBot.getBot(QQLogin.getInstance().getConfig().getLong("bot-qq"));

        List<Long> active_groups = QQLogin.getInstance().getConfig().getLongList("active-groups");
        for (long groupId : bot.getGroupList()) {
            if (active_groups.contains(groupId)) {
                if (bot.getGroup(groupId).contains(qq)) {
                    // 接受好友请求
                    event.setAcceptRequest();
                    return;
                }
            }
        }

        // 拒绝好友请求
        event.setDenyRequest(false);
    }

    // 收到好友消息
    @EventHandler
    public void onFriendMessage(MiraiFriendMessageEvent event) throws SQLException {
        // 判断消息是否为验证码
        if (event.getMessage().length() != QQLogin.getInstance().getConfig().getInt("captcha.length"))
            return;
        // 查询数据库是否对应玩家
        UUID uuid = QQLogin.getInstance().getDb().getUUID(event.getSenderID());
        if (uuid == null) return;

        // 是否是未登录玩家
        if (!QQLogin.getInstance().isUnAuthedPlayer(uuid)) return;

        Captcha captcha = QQLogin.getInstance().getCaptcha(uuid);

        // 是否过期
        if (captcha.isExpired()) return;

        // 是否正确
        if (!captcha.verify(event.getMessage())) return;

        // 是否是绑定QQ号发送的验证码
        if (captcha.getQQ() != event.getSenderID()) return;

        // 校验通过
        QQLogin.getInstance().removeUnAuthedPlayer(uuid);

        // 发送欢迎消息
        Bukkit.getPlayer(uuid).sendTitle("欢迎回来", "登录成功", 10, 30, 10);
    }
}

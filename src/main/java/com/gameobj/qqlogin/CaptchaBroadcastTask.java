package com.gameobj.qqlogin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CaptchaBroadcastTask implements Runnable {
    @Override
    public void run() {
        QQLogin.getInstance().getUnAuthedPlayers().forEach(uuid -> {
                    Player p = Bukkit.getPlayer(uuid);
                    Captcha captcha = QQLogin.getInstance().getCaptcha(uuid);
                    if (p != null) {
                        if (captcha != null) {
                            String title = "§6§l" + captcha.getCaptcha();
                            String subtitle;
                            // 计算验证码剩余时间
                            long time = (captcha.getExpireTime().getTime() - System.currentTimeMillis()) / 1000;
                            if (time > 0) {
                                subtitle = "§e验证码剩余时间: §c" + time + "§e秒";
                            } else {
                                subtitle = "§e验证码已过期";
                                QQLogin.getInstance().setNewCaptcha(uuid, Captcha.create(captcha.getQQ()));
                            }
                            p.sendTitle(title, subtitle, 0, 40, 0);
                        } else {
                            p.sendTitle("§c请绑定QQ号！", "/qqlogin 0123456789", 0, 40, 0);
                        }
                    }
                }
        );
    }
}

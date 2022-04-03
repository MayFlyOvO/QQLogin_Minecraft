package com.gameobj.qqlogin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class CommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                try {
                    long qq = Long.parseLong(args[0]);

                    if (QQLogin.getInstance().getDb().getQQNumber(player.getUniqueId()) == 0) {
                        QQLogin.getInstance().getDb().insert(player.getUniqueId(), qq);
                    } else {
                        QQLogin.getInstance().getDb().update(player.getUniqueId(), qq);
                    }

                    player.sendMessage("§aQQ绑定成功");

                    QQLogin.getInstance().setNewCaptcha(player.getUniqueId(), Captcha.create(qq));

                } catch (NumberFormatException e) {
                    player.sendMessage("请输入正确的QQ号码");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (args.length == 1) {
                if (args[0].equals("reload")) {
                    QQLogin.getInstance().reloadConfig();
                    QQLogin.getInstance().getLogger().info("配置文件已重载");
                }
            }
        }

        return true;
    }
}

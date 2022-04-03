package com.gameobj.qqlogin;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

public class PlayerRestrictions implements Listener {
    // 取消玩家攻击
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (shouldRestrict(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家破坏方块
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家放置方块
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家发送聊天信息
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家发送命令
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            String message = event.getMessage().replaceAll("^/", "").toLowerCase();
            if (!message.startsWith("qqlogin")) {
                event.setCancelled(true);
            }
        }
    }

    // 取消玩家抛弃物品
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家捡起物品
    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (shouldRestrict(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    // 取消玩家移动
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (shouldRestrict(event.getPlayer())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if (to == null || (from.getX() == to.getX() && from.getZ() == to.getZ())) {
                return;
            }
            Vector speed = event.getPlayer().getVelocity();
            from.setY(to.getY());
            from.setYaw(to.getYaw());
            from.setPitch(to.getPitch());
            event.getPlayer().teleport(from);
            event.getPlayer().setVelocity(speed);
        }
    }

    // 取消玩家受到伤害
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (shouldRestrict(event.getEntity())) {
            event.setCancelled(true);
        }
    }


    private boolean shouldRestrict(Entity entity) {
        if (entity.getType() != EntityType.PLAYER) {
            return false;
        }

        Player player = (Player) entity;
        return QQLogin.getInstance().isUnAuthedPlayer(player.getUniqueId());
    }
}

package com.Raking.ASkyBlockEx.Listener;

import com.Raking.ASkyBlockEx.ASkyBlockEx;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class IslandListener implements Listener {

    private final JavaPlugin plugin;
    private final ASkyBlockAPI AskyBlockAPI;
    private final List<String> notItemList;

    public IslandListener(JavaPlugin plugin) {
        this.plugin = plugin;
        AskyBlockAPI = ASkyBlockAPI.getInstance();
        this.notItemList = plugin.getConfig().getStringList("notItem"); // 从配置文件中读取 notItem 列表
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        startChecking(player);
    }

    private void startChecking(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // 检查玩家是否在名为 "ASkyBlock" 的世界中
                if ("ASkyBlock".equals(player.getWorld().getName())) {
                    if (!ASkyBlockEx.AskyblockApi.playerIsOnIsland(player) && (!player.isOp())) {
                        ItemStack itemInHand = player.getInventory().getItemInHand();
                        if (itemInHand != null && notItemList.contains(itemInHand.getType().name())) {
                            player.getInventory().remove(itemInHand);
                            player.sendMessage("别人的岛屿上不允许使用它！已没收！");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 10L); // 每tick检测一次
    }
}
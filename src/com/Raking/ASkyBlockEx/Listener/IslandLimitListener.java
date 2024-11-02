package com.Raking.ASkyBlockEx.Listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IslandLimitListener implements Listener {

    private ASkyBlockAPI skyBlockAPI;
    private Map<String, Integer> entityLimits;
    private Map<EntityType, Integer> entityLimitsClear;
    private Plugin plugin;
    private BukkitTask clearTask;
    private Map<Island, IslandInfo> islandInfoCache;
    private String targetWorldName = "ASkyBlock"; // 指定目标世界名称

    public IslandLimitListener(Plugin plugin) {
        this.plugin = plugin;
        skyBlockAPI = ASkyBlockAPI.getInstance();
        entityLimits = new HashMap<>();
        entityLimitsClear = new HashMap<>();
        islandInfoCache = new ConcurrentHashMap<>();
        // 从配置文件读取实体限制
        loadEntityLimits();
        // 设置定时任务
        setupEntityClearTask();
    }

    private void loadEntityLimits() {
        // 从配置文件读取实体限制
        List<String> limits = plugin.getConfig().getStringList("Entity_limits");
        for (String limit : limits) {
            String[] parts = limit.split("\\|");
            String[] itemParts = parts[0].split(":");
            String[] entityParts = parts[1].split(":");
            entityLimits.put(itemParts[0] + "|" + entityParts[0], Integer.parseInt(entityParts[1].trim()));
        }

        // 从配置文件读取实体清理限制
        List<String> limitsClear = plugin.getConfig().getStringList("Entity_limits_clear");
        for (String limit : limitsClear) {
            String[] parts = limit.split(":");
            entityLimitsClear.put(EntityType.valueOf(parts[0]), Integer.parseInt(parts[1].trim()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !player.getWorld().getName().equals(targetWorldName)) {
            return;
        }

        if (skyBlockAPI.playerIsOnIsland(player) && !player.isOp()) {
            for (Map.Entry<String, Integer> entry : entityLimits.entrySet()) {
                String[] parts = entry.getKey().split("\\|");
                String itemType = parts[0];
                String entityType = parts[1];
                int limit = entry.getValue();

                // 检查物品类型是否匹配
                if (item.getType().name().equals(itemType)) {
                    if (isEntityLimitExceeded(player, EntityType.valueOf(entityType), limit)) {
                        event.setCancelled(true);
                        player.sendMessage("超出此实体上限！");
                        return;
                    }
                }
            }
        }
    }

    private boolean isEntityLimitExceeded(Player player, EntityType entityType, int limit) {
        int count = 0;
        // 获取玩家岛屿对象
        Island island = skyBlockAPI.getIslandAt(player.getLocation());
        if (island == null) {
            return false;
        }

        IslandInfo islandInfo = getIslandInfo(island);
        int minX = islandInfo.minX;
        int maxX = islandInfo.maxX;
        int minZ = islandInfo.minZ;
        int maxZ = islandInfo.maxZ;

        for (org.bukkit.entity.Entity entity : player.getWorld().getEntities()) {
            if (entity.getType() == entityType && isWithinBounds(entity, minX, maxX, minZ, maxZ)) {
                count++;
            }
        }
        return count >= limit;
    }

    private boolean isWithinBounds(org.bukkit.entity.Entity entity, int minX, int maxX, int minZ, int maxZ) {
        int entityX = entity.getLocation().getBlockX();
        int entityZ = entity.getLocation().getBlockZ();
        return entityX >= minX && entityX <= maxX && entityZ >= minZ && entityZ <= maxZ;
    }

    private IslandInfo getIslandInfo(Island island) {
        return islandInfoCache.computeIfAbsent(island, k -> {
            int protectionSize = island.getProtectionSize();
            int centerX = island.getCenter().getBlockX();
            int centerZ = island.getCenter().getBlockZ();
            return new IslandInfo(centerX - protectionSize, centerX + protectionSize, centerZ - protectionSize, centerZ + protectionSize);
        });
    }

    private void setupEntityClearTask() {
        clearTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<EntityType, Integer> entry : entityLimitsClear.entrySet()) {
                    EntityType entityType = entry.getKey();
                    int limit = entry.getValue();
                    clearExcessEntities(entityType, limit);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 5); // 每5秒同步执行一次
    }


    private void clearExcessEntities(EntityType entityType, int limit) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.getWorld().getName().equals(targetWorldName)) {
                continue; // 跳过不在 ASkyBlock 世界的玩家
            }
            Island island = skyBlockAPI.getIslandAt(player.getLocation());
            if (island != null) {
                IslandInfo islandInfo = getIslandInfo(island);
                int minX = islandInfo.minX;
                int maxX = islandInfo.maxX;
                int minZ = islandInfo.minZ;
                int maxZ = islandInfo.maxZ;

                int count = 0;
                for (org.bukkit.entity.Entity entity : player.getWorld().getEntities()) {
                    if (entity.getType() == entityType && isWithinBounds(entity, minX, maxX, minZ, maxZ)) {
                        count++;
                    }
                }

                if (count > limit) {
                    int excess = count - limit;
                    for (org.bukkit.entity.Entity entity : player.getWorld().getEntities()) {
                        if (entity.getType() == entityType && isWithinBounds(entity, minX, maxX, minZ, maxZ)) {
                            // 检查是否是玩家实体
                            if (entity instanceof Player) {
                                continue; // 跳过玩家实体
                            }
                            entity.remove();
                            excess--;
                            if (excess <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static class IslandInfo {
        final int minX, maxX, minZ, maxZ;

        IslandInfo(int minX, int maxX, int minZ, int maxZ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }
}
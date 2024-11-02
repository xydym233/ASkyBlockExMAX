

package com.Raking.ASkyBlockEx;

import com.Raking.ASkyBlockEx.Listener.*;
import com.wasteofplastic.askyblock.ASkyBlockAPI;
import java.util.logging.Level;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ASkyBlockEx extends JavaPlugin {
    public static ASkyBlockAPI AskyblockApi;
    private static World SkyBlockWorld;

    public ASkyBlockEx() {
    }

    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        getServer().getPluginManager().registerEvents(new IslandLimitListener(this), this);
        this.getServer().getPluginManager().registerEvents(new OnBlockBreakEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnBlockPlaceEvent(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerInteractEvent(), this);
        this.getServer().getPluginManager().registerEvents(new IslandListener(this), this);

        this.getServer().getLogger().log(Level.INFO, "[ASkyBlockExMAX] 启动完成~");
        AskyblockApi = ASkyBlockAPI.getInstance();
    }

    public static World getSkyBlockWorld() {
        if (SkyBlockWorld == null) {
            SkyBlockWorld = AskyblockApi.getIslandWorld();
        }

        return SkyBlockWorld;
    }

    public void onDisable() {
        this.getServer().getLogger().log(Level.INFO, "[ASkyBlockExMAX] 卸载完成~");
    }
}

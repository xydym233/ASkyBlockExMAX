//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.Raking.ASkyBlockEx.Listener;

import com.Raking.ASkyBlockEx.ASkyBlockEx;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreakEvent implements Listener {
    public OnBlockBreakEvent() {
    }

    @EventHandler(
            priority = EventPriority.LOW,
            ignoreCancelled = true
    )
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (player.getWorld() == ASkyBlockEx.getSkyBlockWorld()) {
                boolean type = ASkyBlockEx.AskyblockApi.playerIsOnIsland(player);
                if (!type && (!player.isOp())) {
                    event.setCancelled(true);
                }

            }
        }
    }
}

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
import org.bukkit.event.block.BlockPlaceEvent;

public class OnBlockPlaceEvent implements Listener {
    public OnBlockPlaceEvent() {
    }

    @EventHandler(
            priority = EventPriority.LOW,
            ignoreCancelled = true
    )
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (player.getWorld() == ASkyBlockEx.getSkyBlockWorld()) {
                if (!ASkyBlockEx.AskyblockApi.playerIsOnIsland(player) && (!player.isOp())) {
                    event.setCancelled(true);
                }

            }
        }
    }
}

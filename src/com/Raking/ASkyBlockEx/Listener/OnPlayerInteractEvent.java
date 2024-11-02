//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.Raking.ASkyBlockEx.Listener;

import com.Raking.ASkyBlockEx.ASkyBlockEx;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnPlayerInteractEvent implements Listener {
    public OnPlayerInteractEvent() {
    }

    @EventHandler(
            priority = EventPriority.LOW,
            ignoreCancelled = true
    )
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            if (player.getWorld() == ASkyBlockEx.getSkyBlockWorld()) {
                if (!ASkyBlockEx.AskyblockApi.playerIsOnIsland(player) && !player.isOp()) {
                    if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
                        return;
                    }

                    event.setCancelled(true);
                }

            }
        }
    }
}

package com.gotjisan.cobblepokestop.event;

import com.gotjisan.cobblepokestop.block.PokestopBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerTickEvent {
    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 20; // Check every second (20 ticks)

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;
            if (tickCounter >= CHECK_INTERVAL) {
                tickCounter = 0;

                // Check all players
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    PokestopBlock.checkPlayerInRange(player.getWorld(), player);
                }
            }
        });
    }
}
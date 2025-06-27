
package com.gotjisan.cobblepokestop.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PokestopBlockEntity extends BlockEntity {
    // Change from Set to Map to store player UUID and their cooldown end time
    private Map<UUID, Long> playerCooldowns = new HashMap<>();
    private static final long COOLDOWN_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds

    public PokestopBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKESTOP_BLOCK_ENTITY, pos, state);
    }

    public boolean hasPlayerCollected(UUID playerUuid) {
        // Clean up expired cooldowns first
        cleanupExpiredCooldowns();

        Long cooldownEndTime = playerCooldowns.get(playerUuid);
        if (cooldownEndTime == null) {
            return false; // Player has never collected from this pokestop
        }

        return System.currentTimeMillis() < cooldownEndTime;
    }

    public void addPlayerCollected(UUID playerUuid) {
        long currentTime = System.currentTimeMillis();
        long cooldownEndTime = currentTime + COOLDOWN_DURATION;
        playerCooldowns.put(playerUuid, cooldownEndTime);
        markDirty();
    }

    public long getRemainingCooldown(UUID playerUuid) {
        Long cooldownEndTime = playerCooldowns.get(playerUuid);
        if (cooldownEndTime == null) {
            return 0;
        }

        long remainingTime = cooldownEndTime - System.currentTimeMillis();
        return Math.max(0, remainingTime);
    }

    public String getFormattedCooldown(UUID playerUuid) {
        long remainingMs = getRemainingCooldown(playerUuid);
        if (remainingMs <= 0) {
            return "0s";
        }

        long seconds = remainingMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        } else {
            return seconds + "s";
        }
    }

    private void cleanupExpiredCooldowns() {
        long currentTime = System.currentTimeMillis();
        playerCooldowns.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        // Save player cooldowns
        NbtCompound cooldownsNbt = new NbtCompound();
        for (Map.Entry<UUID, Long> entry : playerCooldowns.entrySet()) {
            cooldownsNbt.putLong(entry.getKey().toString(), entry.getValue());
        }
        nbt.put("player_cooldowns", cooldownsNbt);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Load player cooldowns
        playerCooldowns.clear();
        if (nbt.contains("player_cooldowns")) {
            NbtCompound cooldownsNbt = nbt.getCompound("player_cooldowns");
            for (String key : cooldownsNbt.getKeys()) {
                try {
                    UUID playerUuid = UUID.fromString(key);
                    long cooldownEndTime = cooldownsNbt.getLong(key);
                    playerCooldowns.put(playerUuid, cooldownEndTime);
                } catch (IllegalArgumentException e) {
                    // Skip invalid UUID
                }
            }
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
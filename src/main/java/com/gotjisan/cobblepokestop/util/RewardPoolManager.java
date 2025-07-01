package com.gotjisan.cobblepokestop.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gotjisan.cobblepokestop.Cobblepokestop;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RewardPoolManager {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "poke_pools.json";
    private static List<RewardPool> cachedPools = null;

    public static List<RewardPool> loadRewardPools() {
        // Return cached pools if already loaded
        if (cachedPools != null) {
            return cachedPools;
        }

        try {
            Path configDir = FabricLoader.getInstance().getConfigDir();
            Path configFile = configDir.resolve(CONFIG_FILE);

            // Create default config if it doesn't exist
            if (!Files.exists(configFile)) {
                createDefaultConfig(configFile);
            }

            // Read and parse JSON file
            String jsonContent = Files.readString(configFile);
            Type listType = new TypeToken<List<RewardPool>>(){}.getType();
            cachedPools = GSON.fromJson(jsonContent, listType);

            if (cachedPools == null) {
                cachedPools = new ArrayList<>();
            }

            Cobblepokestop.LOGGER.info("Loaded " + cachedPools.size() + " reward pools from config");
            return cachedPools;

        } catch (Exception e) {
            Cobblepokestop.LOGGER.error("Failed to load reward pools: " + e.getMessage());
            return getDefaultRewardPools();
        }
    }

    private static void createDefaultConfig(Path configFile) {
        try {
            // Create default reward pools
            List<RewardPool> defaultPools = getDefaultRewardPools();

            // Convert to JSON
            String jsonContent = GSON.toJson(defaultPools);

            // Write to file
            Files.writeString(configFile, jsonContent);

            Cobblepokestop.LOGGER.info("Created default poke_pools.json config file");

        } catch (IOException e) {
            Cobblepokestop.LOGGER.error("Failed to create default config: " + e.getMessage());
        }
    }

    private static List<RewardPool> getDefaultRewardPools() {
        List<RewardPool> defaultPools = new ArrayList<>();
        defaultPools.add(new RewardPool("minecraft:apple", 5, 1));
        defaultPools.add(new RewardPool("minecraft:diamond", 1, 1));
        return defaultPools;
    }

    // Method to reload config (useful for live reloading)
    public static void reloadRewardPools() {
        cachedPools = null;
        loadRewardPools();
        Cobblepokestop.LOGGER.info("Reward pools reloaded");
    }

    // Method to get cached pools without reloading
    public static List<RewardPool> getCachedPools() {
        return cachedPools != null ? cachedPools : loadRewardPools();
    }
}
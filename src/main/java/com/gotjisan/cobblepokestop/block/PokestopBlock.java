package com.gotjisan.cobblepokestop.block;

import com.gotjisan.cobblepokestop.Cobblepokestop;
import com.gotjisan.cobblepokestop.block.entity.PokestopBlockEntity;
import com.gotjisan.cobblepokestop.util.RewardPool;
import com.gotjisan.cobblepokestop.util.RewardPoolManager;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PokestopBlock extends Block implements BlockEntityProvider {
    public static final Block POKE_BLOCK = registerBlock("poke_block",
            new PokestopBlock(AbstractBlock.Settings.create().strength(4f)
                    .requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public PokestopBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PokestopBlockEntity(pos, state);
    }

    // Method to check if player is in range and give rewards
    public static void checkPlayerInRange(World world, PlayerEntity player) {
        if (world.isClient) return;

        BlockPos playerPos = player.getBlockPos();

        // Check 9x9 area around player
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    BlockState blockState = world.getBlockState(checkPos);

                    if (blockState.getBlock() instanceof PokestopBlock) {
                        BlockEntity blockEntity = world.getBlockEntity(checkPos);
                        if (blockEntity instanceof PokestopBlockEntity pokestopEntity) {

                            if (!pokestopEntity.hasPlayerCollected(player.getUuid())) {
                                giveRewards(world, player, checkPos);
                                pokestopEntity.addPlayerCollected(player.getUuid());

                                // Play sound and send message
                                world.playSound(null, checkPos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                        SoundCategory.BLOCKS, 1.0f, 1.0f);
                                player.sendMessage(Text.literal("§aYou collected rewards from the Pokestop!"), true);
                            } else {
                                // Player is on cooldown, show remaining time
                                String remainingTime = pokestopEntity.getFormattedCooldown(player.getUuid());
                                player.sendMessage(Text.literal("§cPokestop is on cooldown! Time remaining: §e" + remainingTime), true);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void giveRewards(World world, PlayerEntity player, BlockPos pos) {
        try {
            // Load reward pools from JSON
            List<RewardPool> rewardPools = RewardPoolManager.loadRewardPools();

            if (rewardPools.isEmpty()) {
                Cobblepokestop.LOGGER.warn("No reward pools found! Using fallback rewards.");
                giveFallbackRewards(player);
                return;
            }

            // Give rewards from all pools based on their weight
            for (RewardPool pool : rewardPools) {
                // Use weight as number of rolls/chances to get this item
                for (int i = 0; i < pool.getWeight(); i++) {
                    // Get item from registry
                    Item item = Registries.ITEM.get(Identifier.tryParse(pool.getItem()));

                    if (item != null && item != Items.AIR) {
                        ItemStack reward = new ItemStack(item, pool.getCount());

                        // Give item to player
                        if (!player.getInventory().insertStack(reward)) {
                            player.dropItem(reward, false);
                        }

                        Cobblepokestop.LOGGER.info("Gave player: " + pool.getItem() + " x" + pool.getCount() + " (roll " + (i + 1) + "/" + pool.getWeight() + ")");
                    } else {
                        Cobblepokestop.LOGGER.warn("Invalid item: " + pool.getItem() + ". Skipping this reward.");
                    }
                }
            }

        } catch (Exception e) {
            Cobblepokestop.LOGGER.error("Error loading reward pools: " + e.getMessage());
            giveFallbackRewards(player);
        }
    }

    // Fallback method if reward pools fail to load
    private static void giveFallbackRewards(PlayerEntity player) {
        ItemStack[] fallbackItems = {
                new ItemStack(Items.APPLE, 1),
                new ItemStack(Items.GOLD_INGOT, 1),
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(Items.EMERALD, 1)
        };

        // Random reward
        ItemStack reward = fallbackItems[player.getWorld().getRandom().nextInt(fallbackItems.length)];

        if (!player.getInventory().insertStack(reward)) {
            player.dropItem(reward, false);
        }

        Cobblepokestop.LOGGER.info("Gave fallback reward: " + reward.getItem().toString());
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(Cobblepokestop.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(Cobblepokestop.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlock() {
        Cobblepokestop.LOGGER.info("Registering Mod Blocks for " + Cobblepokestop.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(PokestopBlock.POKE_BLOCK);
        });
    }
}
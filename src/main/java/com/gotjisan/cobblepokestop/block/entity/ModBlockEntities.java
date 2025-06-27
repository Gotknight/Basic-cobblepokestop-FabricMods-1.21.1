package com.gotjisan.cobblepokestop.block.entity;

import com.gotjisan.cobblepokestop.Cobblepokestop;
import com.gotjisan.cobblepokestop.block.PokestopBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<PokestopBlockEntity> POKESTOP_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(Cobblepokestop.MOD_ID, "pokestop_block_entity"),
                    BlockEntityType.Builder.create(PokestopBlockEntity::new,
                            PokestopBlock.POKE_BLOCK).build());

    public static void registerBlockEntities() {
        Cobblepokestop.LOGGER.info("Registering Block Entities for " + Cobblepokestop.MOD_ID);
    }
}
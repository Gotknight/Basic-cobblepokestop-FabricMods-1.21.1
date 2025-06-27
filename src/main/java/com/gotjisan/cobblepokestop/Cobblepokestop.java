package com.gotjisan.cobblepokestop;

import com.gotjisan.cobblepokestop.block.PokestopBlock;
import com.gotjisan.cobblepokestop.block.entity.ModBlockEntities;
import com.gotjisan.cobblepokestop.event.PlayerTickEvent;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Cobblepokestop implements ModInitializer {
	public static final String MOD_ID = "cobblepokestop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PokestopBlock.registerModBlock();
		ModBlockEntities.registerBlockEntities();
		PlayerTickEvent.register();
	}

}
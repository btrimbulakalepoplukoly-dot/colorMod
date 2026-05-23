package com.colormod.client;

import com.colormod.client.gui.HudButtonRenderer;
import com.colormod.config.ColorConfig;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorModClient implements ClientModInitializer {

    public static final String MOD_ID = "colormod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[ColorMod] Initialising...");

        // Load saved config (creates defaults if missing)
        ColorConfig.load();

        // Register HUD button + in-game tint overlay
        HudButtonRenderer.register();

        LOGGER.info("[ColorMod] Ready. Config loaded from disk.");
    }
}

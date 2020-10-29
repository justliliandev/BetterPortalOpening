package com.agnor99.better_portal_opening;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterPortalOpening.MOD_ID)
public class BetterPortalOpening {
    public static final String MOD_ID = "better_portal_opening";

    public BetterPortalOpening() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PortalOpener.class);
    }
}

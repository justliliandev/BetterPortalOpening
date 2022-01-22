package dev.agnor99.better_portal_opening;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterPortalOpening.MOD_ID)
public class BetterPortalOpening {
    public static final String MOD_ID = "better_portal_opening";

    public BetterPortalOpening() {
        MinecraftForge.EVENT_BUS.register(PortalOpener.class);
    }
}

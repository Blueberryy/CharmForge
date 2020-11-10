package svenhjol.charm.module;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GoldBarsBlock;

@Module(mod = Charm.MOD_ID, description = "Gold variant of vanilla iron bars.")
public class GoldBars extends CharmModule {
    public static GoldBarsBlock GOLD_BARS;

    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        GOLD_BARS = new GoldBarsBlock(this);
        depends(!ModHelper.isLoaded("quark") || override);
    }

    @Override
    public void clientInit() {
        RenderTypeLookup.setRenderLayer(GOLD_BARS, RenderType.getCutout());
    }
}

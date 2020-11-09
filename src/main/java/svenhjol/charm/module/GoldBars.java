package svenhjol.charm.module;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GoldBarsBlock;

@Module(mod = Charm.MOD_ID, description = "Gold variant of vanilla iron bars.")
public class GoldBars extends CharmModule {
    public static GoldBarsBlock GOLD_BARS;

    @Override
    public void register() {
        GOLD_BARS = new GoldBarsBlock(this);
    }

    @Override
    public void clientInit() {
        RenderTypeLookup.setRenderLayer(GOLD_BARS, RenderType.getCutout());
    }
}

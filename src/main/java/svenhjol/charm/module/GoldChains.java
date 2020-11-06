package svenhjol.charm.module;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GoldChainBlock;

@Module(mod = Charm.MOD_ID, description = "Gold version of the vanilla chain.")
public class GoldChains extends CharmModule {
    public static GoldChainBlock GOLD_CHAIN;

    @Override
    public void register() {
        GOLD_CHAIN = new GoldChainBlock(this);
    }

    @Override
    public void clientRegister() {
        RenderTypeLookup.setRenderLayer(GOLD_CHAIN, RenderType.getCutout());
    }
}

package svenhjol.charm.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.GoldLanterns;

public class GoldLanternsClient extends CharmClientModule {
    public GoldLanternsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void init() {
        RenderTypeLookup.setRenderLayer(GoldLanterns.GOLD_LANTERN, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(GoldLanterns.GOLD_SOUL_LANTERN, RenderType.getCutout());
    }
}

package svenhjol.charm.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.RedstoneLanterns;

public class RedstoneLanternsClient extends CharmClientModule {
    public RedstoneLanternsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void init() {
        RenderTypeLookup.setRenderLayer(RedstoneLanterns.REDSTONE_LANTERN, RenderType.getCutout());
    }
}

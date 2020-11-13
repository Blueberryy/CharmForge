package svenhjol.charm.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.module.RedstoneLanterns;

public class RedstoneLanternsClient extends CharmClientModule {
    public RedstoneLanternsClient(RedstoneLanterns module) {
        super(module);
    }

    @Override
    public void register() {
        RenderTypeLookup.setRenderLayer(RedstoneLanterns.REDSTONE_LANTERN, RenderType.getCutout());
    }
}

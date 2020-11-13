package svenhjol.charm.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.VariantLadders;

public class VariantLaddersClient extends CharmClientModule {
    public VariantLaddersClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        VariantLadders.LADDER_BLOCKS.values().forEach(ladder -> {
            RenderTypeLookup.setRenderLayer(ladder, RenderType.getCutout());
        });
    }
}

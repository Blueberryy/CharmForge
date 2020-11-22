package svenhjol.charm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.Mooblooms;
import svenhjol.charm.render.MoobloomEntityRenderer;

public class MoobloomsClient extends CharmClientModule {
    public MoobloomsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(Mooblooms.MOOBLOOM, MoobloomEntityRenderer::new);
    }
}

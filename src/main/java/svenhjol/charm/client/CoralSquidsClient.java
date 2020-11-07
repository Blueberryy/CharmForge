package svenhjol.charm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.module.CoralSquids;
import svenhjol.charm.render.CoralSquidEntityRenderer;
import svenhjol.charm.base.CharmModule;

public class CoralSquidsClient {
    public CoralSquidsClient(CharmModule module) {
        RenderingRegistry.registerEntityRenderingHandler(CoralSquids.CORAL_SQUID, CoralSquidEntityRenderer::new);
    }
}

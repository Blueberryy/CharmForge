package svenhjol.charm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.EndermitePowder;
import svenhjol.charm.render.EndermitePowderRenderer;

public class EndermitePowderClient {
    public EndermitePowderClient(CharmModule module) {
        RenderingRegistry.registerEntityRenderingHandler(EndermitePowder.ENTITY, EndermitePowderRenderer::new);
    }
}

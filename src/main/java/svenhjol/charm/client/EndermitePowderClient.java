package svenhjol.charm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.module.EndermitePowder;
import svenhjol.charm.render.EndermitePowderRenderer;

public class EndermitePowderClient extends CharmClientModule {
    public EndermitePowderClient(EndermitePowder module) {
        super(module);
    }

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(EndermitePowder.ENTITY, EndermitePowderRenderer::new);
    }
}

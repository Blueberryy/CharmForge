package svenhjol.charm.client;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.module.CoralSquids;
import svenhjol.charm.render.CoralSquidEntityRenderer;

public class CoralSquidsClient extends CharmClientModule {
    public CoralSquidsClient(CoralSquids module) {
        super(module);
    }

    @Override
    public void register() {
        RenderingRegistry.registerEntityRenderingHandler(CoralSquids.CORAL_SQUID, CoralSquidEntityRenderer::new);
    }
}

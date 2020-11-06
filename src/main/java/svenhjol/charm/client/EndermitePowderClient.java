package svenhjol.charm.client;

import svenhjol.charm.module.EndermitePowder;
import svenhjol.charm.base.CharmModule;

public class EndermitePowderClient {
    public EndermitePowderClient(CharmModule module) {
        EntityRendererRegistry.INSTANCE.register(EndermitePowder.ENTITY, ((dispatcher, context)
            -> new EndermiteEntityRenderer(dispatcher)));
    }
}

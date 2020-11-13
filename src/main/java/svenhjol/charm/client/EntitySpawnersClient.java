package svenhjol.charm.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.EntitySpawners;

public class EntitySpawnersClient extends CharmClientModule {
    public EntitySpawnersClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        RenderTypeLookup.setRenderLayer(EntitySpawners.ENTITY_SPAWNER, RenderType.getCutout());
    }
}

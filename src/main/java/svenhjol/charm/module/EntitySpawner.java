package svenhjol.charm.module;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ClientRegistryHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.EntitySpawnerBlock;
import svenhjol.charm.blockentity.EntitySpawnerBlockEntity;

@Module(mod = Charm.MOD_ID, description = "Spawns entities when a player is within range.", alwaysEnabled = true)
public class EntitySpawner extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "entity_spawner");
    public static EntitySpawnerBlock ENTITY_SPAWNER;
    public static TileEntityType<EntitySpawnerBlockEntity> BLOCK_ENTITY;

    @Config(name = "Trigger distance", description = "Player will trigger EntitySpawner blocks when closer than this distance.")
    public static int triggerDistance = 16;

    @Override
    public void register() {
        ENTITY_SPAWNER = new EntitySpawnerBlock(this);
        BLOCK_ENTITY = RegistryHandler.blockEntity(ID, EntitySpawnerBlockEntity::new, ENTITY_SPAWNER);
    }

    @Override
    public void clientRegister() {
        ClientRegistryHandler.setRenderLayer(ENTITY_SPAWNER, RenderType.getCutout());
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.GlowBallsClient;
import svenhjol.charm.entity.GlowBallEntity;
import svenhjol.charm.item.GlowBallItem;

@Module(mod = Charm.MOD_ID, description = "Glow Balls can be thrown to produce a light source where they impact.")
public class GlowBalls extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "glow_ball");
    public static GlowBallItem GLOW_BALL;
    public static EntityType<GlowBallEntity> ENTITY;

    public static GlowBallsClient client;

    @Override
    public void register() {
        GLOW_BALL = new GlowBallItem(this);

        ENTITY = RegistryHandler.entity(ID, EntityType.Builder.<GlowBallEntity>create(GlowBallEntity::new, EntityClassification.MISC)
            .trackingRange(4)
            .setUpdateInterval(10)
            .size(0.25F, 0.25F)
            .build(ID.getPath()));

        depends(ModuleHandler.enabled("charm:placeable_glowstone_dust"));
    }

    @Override
    public void clientRegister() {
        client = new GlowBallsClient(this);
    }
}

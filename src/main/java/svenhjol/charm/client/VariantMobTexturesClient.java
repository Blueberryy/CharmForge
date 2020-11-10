package svenhjol.charm.client;

import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.module.VariantMobTextures;
import svenhjol.charm.render.VariantMobRenderer;

public class VariantMobTexturesClient {
    public VariantMobTexturesClient(CharmModule module) {
        if (!module.enabled)
            return;

        if (VariantMobTextures.variantChickens && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.CHICKEN, VariantMobRenderer.Chicken::new);

        if (VariantMobTextures.variantCows && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.COW, VariantMobRenderer.Cow::new);

        if (VariantMobTextures.variantPigs && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.PIG, VariantMobRenderer.Pig::new);

        if (VariantMobTextures.variantSheep)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SHEEP, VariantMobRenderer.Sheep::new);

        if (VariantMobTextures.variantSnowGolems)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SNOW_GOLEM, VariantMobRenderer.SnowGolem::new);

        if (VariantMobTextures.variantSquids)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SQUID, VariantMobRenderer.Squid::new);

        if (VariantMobTextures.variantWolves)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.WOLF, VariantMobRenderer.Wolf::new);

    }
}

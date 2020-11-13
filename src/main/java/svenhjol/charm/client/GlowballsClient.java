package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.Glowballs;

public class GlowballsClient extends CharmClientModule {
    public GlowballsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        RenderTypeLookup.setRenderLayer(Glowballs.GLOWBALL_BLOCK, RenderType.getTranslucent());
        RenderingRegistry.registerEntityRenderingHandler(Glowballs.GLOWBALL, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}

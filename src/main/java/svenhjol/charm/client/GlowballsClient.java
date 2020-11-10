package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.Glowballs;

public class GlowballsClient {
    public GlowballsClient(CharmModule module) {
        RenderingRegistry.registerEntityRenderingHandler(Glowballs.GLOWBALL, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}

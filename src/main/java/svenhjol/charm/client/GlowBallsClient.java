package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.GlowBalls;

public class GlowBallsClient {
    public GlowBallsClient(CharmModule module) {
        RenderingRegistry.registerEntityRenderingHandler(GlowBalls.ENTITY, manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }
}

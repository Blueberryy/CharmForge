package svenhjol.charm.mixin;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.client.SnowStormsClient;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private ClientWorld world;

    private float gradient;

    @Inject(
        method = "renderRainSnow",
        at = @At("HEAD")
    )
    private void hookRenderWeather(LightTexture manager, float f, double d, double e, double g, CallbackInfo ci) {
        gradient = f;
    }

    @Redirect(
        method = "renderRainSnow",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V",
            ordinal = 1
        )
    )
    private void hookRenderWeatherTexture(TextureManager textureManager, ResourceLocation id) {
        if (!SnowStormsClient.tryHeavySnowTexture(world, textureManager, gradient))
            textureManager.bindTexture(id);
    }
}

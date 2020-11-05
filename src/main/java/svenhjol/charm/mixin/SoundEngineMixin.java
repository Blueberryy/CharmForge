package svenhjol.charm.mixin;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/audio/AudioStreamManager;createResource(Lnet/minecraft/util/ResourceLocation;)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStatic(ISound soundInstance, CallbackInfo ci) {
        // TODO: forge event for this
        // PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }

    @Inject(
        method = "play",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/audio/AudioStreamManager;createStreamingResource(Lnet/minecraft/util/ResourceLocation;Z)Ljava/util/concurrent/CompletableFuture;"
        )
    )
    private void hookPlayStreamed(ISound soundInstance, CallbackInfo ci) {
        // TODO: forge event for this
        // PlaySoundCallback.EVENT.invoker().interact((SoundEngine)(Object)this, soundInstance);
    }
}

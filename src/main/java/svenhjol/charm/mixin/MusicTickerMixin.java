package svenhjol.charm.mixin;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.client.MusicImprovementsClient;

@Mixin(MusicTicker.class)
public class MusicTickerMixin {
    @Shadow private ISound currentMusic;

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookTick(CallbackInfo ci) {
        if (MusicImprovementsClient.isEnabled && MusicImprovementsClient.handleTick(this.currentMusic))
            ci.cancel();
    }

    @Inject(
        method = "stop",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookStop(CallbackInfo ci) {
        if (MusicImprovementsClient.isEnabled && MusicImprovementsClient.handleStop())
            ci.cancel();
    }

    @Inject(
        method = "isBackgroundMusicPlaying",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookIsPlayingType(BackgroundMusicSelector music, CallbackInfoReturnable<Boolean> cir) {
        if (MusicImprovementsClient.isEnabled && MusicImprovementsClient.handlePlaying(music))
            cir.setReturnValue(true);
    }
}

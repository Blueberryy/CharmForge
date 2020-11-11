package svenhjol.charm.mixin;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.handler.ColoredGlintHandler;
import svenhjol.charm.module.Core;

@Mixin(RenderType.class)
public class RenderTypeMixin {
    @Inject(
        method = "getArmorGlint",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetArmorGlint(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getArmorGlintRenderLayer());
    }

    @Inject(
        method = "getArmorEntityGlint",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetArmorEntityGlint(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getArmorEntityGlintRenderLayer());
    }

    @Inject(
        method = "getEntityGlint",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetEntityGlint(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getEntityGlintRenderLayer());
    }

    @Inject(
        method = "getEntityGlintDirect",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetEntityGlintDirect(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getDirectEntityGlintRenderLayer());
    }

    @Inject(
        method = "getGlint",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetGlint(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getGlintRenderLayer());
    }

    @Inject(
        method = "getGlintDirect",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetGlintDirect(CallbackInfoReturnable<RenderType> cir) {
        if (Core.debug)
            cir.setReturnValue(ColoredGlintHandler.getDirectGlintRenderLayer());
    }
}

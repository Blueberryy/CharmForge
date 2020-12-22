package svenhjol.charm.mixin;

import net.minecraft.entity.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.MobSoundCulling;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Shadow public int livingSoundTime;

    @Inject(
        method = "func_241821_eG",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookGetMinAmbientSoundDelay(CallbackInfo ci) {
        if (ModuleHandler.enabled(MobSoundCulling.class)) {
            int val = MobSoundCulling.getMinAmbientSoundDelay((MobEntity) (Object) this);
            if (val > -1) {
                this.livingSoundTime = -val;
                ci.cancel();
            }
        }

        // fall-through to the mob's default delay
    }
}

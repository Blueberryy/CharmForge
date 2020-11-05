package svenhjol.charm.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import svenhjol.charm.module.WanderingTraderImprovements;

import java.util.Random;

@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderSpawnerMixin {
    @Inject(
        method = "func_234562_a_",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/server/ServerWorld;getPointOfInterestManager()Lnet/minecraft/village/PointOfInterestManager;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void hookTraderSpawn(ServerWorld serverWorld, CallbackInfoReturnable<Boolean> cir, PlayerEntity player) {
        if (!WanderingTraderImprovements.checkSpawnConditions(serverWorld, player.getPosition()))
            cir.setReturnValue(false);
    }

    @Redirect(
        method = "func_234562_a_",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextInt(I)I"
        )
    )
    private int hookRandomCheck(Random random, int i) {
        return WanderingTraderImprovements.shouldSpawnFrequently() ? 0 : 10; // 10 is vanilla random value
    }
}

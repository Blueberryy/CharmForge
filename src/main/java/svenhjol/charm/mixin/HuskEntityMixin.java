package svenhjol.charm.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.module.HuskImprovements;

import java.util.Random;

@Mixin(HuskEntity.class)
public abstract class HuskEntityMixin {
    @Inject(
        method = "func_223334_b",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void hookCanSpawn(EntityType<HuskEntity> entity, IServerWorld world, SpawnReason reason, BlockPos pos, Random rand, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && HuskImprovements.canSpawn())
            cir.setReturnValue(HuskEntity.canMonsterSpawnInLight(entity, world, reason, pos, rand));
    }
}

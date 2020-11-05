package svenhjol.charm.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.module.StrayImprovements;

import java.util.Random;

@Mixin(StrayEntity.class)
public abstract class StrayEntityMixin {
    @Inject(
        method = "func_223327_b",
        at = @At("RETURN"),
        cancellable = true
    )
    private static void hookCanSpawn(EntityType<StrayEntity> entity, IServerWorld world, SpawnReason reason, BlockPos pos, Random rand, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && StrayImprovements.canSpawn())
            cir.setReturnValue(StrayEntity.canMonsterSpawnInLight(entity, world, reason, pos, rand));
    }
}

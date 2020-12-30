package svenhjol.charm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.module.AerialAffinity;
import svenhjol.charm.module.ParrotsStayOnShoulder;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
    @Shadow private long timeEntitySatOnShoulder;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
        method = "spawnShoulderEntities",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookSpawnShoulderEntities(CallbackInfo ci) {
        if (ParrotsStayOnShoulder.shouldParrotStayMounted(this.world, this.timeEntitySatOnShoulder))
            ci.cancel();
    }

    @Redirect(method = "getDigSpeed(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)F",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;onGround:Z"))
    private boolean hookDigSpeedOnGround(PlayerEntity player, BlockState state, BlockPos pos) {
        return player.isOnGround() || AerialAffinity.digFast(player);
    }
}

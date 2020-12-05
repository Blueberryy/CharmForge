package svenhjol.charm.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import svenhjol.charm.module.Acquisition;

@Mixin(PlayerInteractionManager.class)
public class PlayerInteractionManagerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow public ServerWorld world;

    /**
     * This differs from Fabric's implementation of the hook as Forge
     * has additional code responsible for dropping XP directly
     * after this mixin target.
     */
    @Inject(
        method = "tryHarvestBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;harvestBlock(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/item/ItemStack;)V"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    private void hookTryBreakBlockAfterBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockState state, int xp, TileEntity blockEntity) {
        if (Acquisition.tryOverrideBreakBlock(world, player, pos, state, blockEntity, xp))
            cir.setReturnValue(false);
    }
}

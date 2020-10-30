package svenhjol.charm.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.BarrelTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.base.CharmTags;

@Mixin(BarrelTileEntity.class)
public class BarrelTileEntityMixin {
    @Redirect(
        method = "barrelTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/block/Block;)Z"
        )
    )
    private boolean hookTickCheckBlockState(BlockState blockState, Block block) {
        return blockState.isIn(CharmTags.BARRELS);
    }
}

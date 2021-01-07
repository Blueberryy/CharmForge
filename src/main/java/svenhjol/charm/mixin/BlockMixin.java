package svenhjol.charm.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.module.Acquisition;

import javax.annotation.Nullable;

@Mixin(Block.class)
public class BlockMixin {


    @Inject(method = "harvestBlock",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;spawnDrops(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V"),
    cancellable = true)
    public void hookHarvestBlock(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack, CallbackInfo callbackInfo) {
        if (!world.isRemote && Acquisition.tryOverrideBreakBlock((ServerWorld) world, player, pos, state, te)) {
            callbackInfo.cancel();
        }
    }
}

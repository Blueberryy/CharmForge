package svenhjol.charm.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.module.Atlas;

@Mixin(CartographyContainer.class)
public abstract class CartographyContainerMixin {
    @Shadow @Final private CraftResultInventory field_217001_f;

    @Shadow @Final private IWorldPosCallable worldPosCallable;

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V", at = @At("TAIL"))
    private void hookConstructor(int syncId, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, CallbackInfo callbackInfo) {
        Atlas.setupAtlasUpscale(playerInventory, (CartographyContainer) (Object) this);
    }

    @Inject(method = "func_216993_a", at = @At("HEAD"), cancellable = true)
    private void hookMakeOutput(ItemStack topStack, ItemStack bottomStack, ItemStack outputStack, CallbackInfo callbackInfo) {
        World world = worldPosCallable.apply((w, b) -> w).orElse(null);
        if (world == null) return;
        if (Atlas.makeAtlasUpscaleOutput(topStack, bottomStack, outputStack, world, field_217001_f, (CartographyContainer) (Object) this)) {
            callbackInfo.cancel();
        }
    }
}

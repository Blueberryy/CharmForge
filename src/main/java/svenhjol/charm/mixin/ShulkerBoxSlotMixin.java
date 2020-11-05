package svenhjol.charm.mixin;

import net.minecraft.inventory.container.ShulkerBoxSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.module.Crates;

@Mixin(ShulkerBoxSlot.class)
public class ShulkerBoxSlotMixin {
    @Inject(
        method = "isItemValid",
        at = @At("HEAD"),
        cancellable = true
    )
    private void isItemValidHook(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!Crates.canShulkerBoxInsertItem(stack))
            cir.setReturnValue(false);
    }
}

package svenhjol.charm.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.container.BookcaseContainer;
import svenhjol.charm.container.CrateContainer;
import vazkii.quark.base.handler.InventoryTransferHandler;

@Mixin(InventoryTransferHandler.class)
public class InventoryTransferHandlerMixin {
    @Inject(
        method = "accepts",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void hookAccepts(Container container, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (container instanceof CrateContainer || container instanceof BookcaseContainer)
            cir.setReturnValue(true);
    }
}

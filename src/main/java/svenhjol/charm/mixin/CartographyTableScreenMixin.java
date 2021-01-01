package svenhjol.charm.mixin;

import net.minecraft.client.gui.screen.inventory.CartographyTableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import svenhjol.charm.module.Atlas;

@Mixin(CartographyTableScreen.class)
public class CartographyTableScreenMixin {

    @ModifyArg(method = "drawGuiContainerBackgroundLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/CartographyTableScreen;func_238807_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/world/storage/MapData;ZZZZ)V"),
            index = 3
    )
    private boolean hookDrawGuiContainerBackgroundLayer(boolean value) {
        if (Atlas.shouldDrawAtlasCopy((CartographyTableScreen) (Object) this)) {
            return true;
        }
        return value;
    }
}

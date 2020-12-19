package svenhjol.charm.mixin;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.StackablePotions;

@Mixin(BrewingRecipeRegistry.class)
public class BrewingRecipeRegistryMixin {

    @Redirect(method = "isValidInput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I"))
    private static int hookGetCount(ItemStack itemStack) {
        if (ModuleHandler.enabled(StackablePotions.class)) return 1;
        return itemStack.getCount();
    }
}

package svenhjol.charm.mixin;

import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.item.crafting.IRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.module.Woodcutters;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    /**
     * Prevents log spam from the recipe book when the woodcutter recipe type cannot be found.
     */
    @Inject(
        method = "getCategory",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetGroupForRecipe(IRecipe<?> recipe, CallbackInfoReturnable<RecipeBookCategories> cir) {
        if (recipe.getType() == Woodcutters.RECIPE_TYPE)
            cir.setReturnValue(RecipeBookCategories.STONECUTTER);

        if (recipe.getType() == Kilns.RECIPE_TYPE)
            cir.setReturnValue(RecipeBookCategories.FURNACE_MISC);
    }
}

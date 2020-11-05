package svenhjol.charm.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.module.Kilns;

public class FiringRecipe extends AbstractCookingRecipe {
    public FiringRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(Kilns.RECIPE_TYPE, id, group, input, output, experience, cookTime);
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Kilns.KILN);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Kilns.RECIPE_SERIALIZER;
    }
}

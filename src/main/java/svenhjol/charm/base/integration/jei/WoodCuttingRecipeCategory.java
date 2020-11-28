package svenhjol.charm.base.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.module.Woodcutters;
import svenhjol.charm.recipe.WoodcuttingRecipe;

class WoodCuttingRecipeCategory implements IRecipeCategory<WoodcuttingRecipe> {
    public static final ResourceLocation UID = Woodcutters.RECIPE_ID;
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public WoodCuttingRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
        background = guiHelper.createDrawable(location, 0, 220, 82, 34);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Woodcutters.WOODCUTTER));
        localizedName = I18n.format("block.charm.woodcutter");
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends WoodcuttingRecipe> getRecipeClass() {
        return WoodcuttingRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(WoodcuttingRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WoodcuttingRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 0, 8);
        guiItemStacks.init(1, false, 60, 8);
        guiItemStacks.set(ingredients);
    }
}

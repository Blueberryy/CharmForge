package svenhjol.charm.base.integration.jei;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.recipe.FiringRecipe;

public class FiringRecipeCategory implements IRecipeCategory<FiringRecipe> {
    public static final ResourceLocation UID = Kilns.RECIPE_ID;
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;
    private final IDrawableStatic staticFlame;
    private final IDrawableAnimated animatedFlame;

    public FiringRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");
        background = guiHelper.createDrawable(location, 0, 114, 82, 54);
        icon = guiHelper.createDrawableIngredient(new ItemStack(Kilns.KILN));
        localizedName = I18n.format("block.charm.kiln");
        staticFlame = guiHelper.createDrawable(location, 82, 114, 14, 14);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
        cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer, IDrawableAnimated>() {
            public IDrawableAnimated load(Integer cookTime) {
                return guiHelper.drawableBuilder(location, 82, 128, 24, 17).buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
            }
        });
    }

    private IDrawableAnimated getArrow(FiringRecipe recipe) {
        int cookTime = recipe.getCookTime();
        if (cookTime <= 0) {
            cookTime = 100;
        }

        return cachedArrows.getUnchecked(cookTime);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends FiringRecipe> getRecipeClass() {
        return FiringRecipe.class;
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
    public void setIngredients(FiringRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void draw(FiringRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        animatedFlame.draw(matrixStack, 1, 20);
        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(matrixStack, 24, 18);
        drawExperience(recipe, matrixStack, 0);
        drawCookTime(recipe, matrixStack, 45);
    }

    private void drawExperience(FiringRecipe recipe, MatrixStack matrixStack, int y) {
        float experience = recipe.getExperience();
        if (experience > 0.0F) {
            TranslationTextComponent experienceString = new TranslationTextComponent("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontRenderer = minecraft.fontRenderer;
            int stringWidth = fontRenderer.getStringPropertyWidth(experienceString);
            fontRenderer.func_243248_b(matrixStack, experienceString, (float)(background.getWidth() - stringWidth), (float)y, -8355712);
        }

    }

    private void drawCookTime(FiringRecipe recipe, MatrixStack matrixStack, int y) {
        int cookTime = recipe.getCookTime();
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            TranslationTextComponent timeString = new TranslationTextComponent("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer fontRenderer = minecraft.fontRenderer;
            int stringWidth = fontRenderer.getStringPropertyWidth(timeString);
            fontRenderer.func_243248_b(matrixStack, timeString, (float)(background.getWidth() - stringWidth), (float)y, -8355712);
        }
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, FiringRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 0, 0);
        guiItemStacks.init(2, false, 60, 18);
        guiItemStacks.set(ingredients);
    }
}

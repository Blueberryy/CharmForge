package svenhjol.charm.base.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.DecreaseRepairCost;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.module.NetheriteNuggets;
import svenhjol.charm.module.Woodcutters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JeiPlugin
public class CharmJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(Charm.MOD_ID, Charm.MOD_ID);

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        final IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        if (ModuleHandler.enabled(DecreaseRepairCost.class))
            registerReduceRepairCost(registration, factory);

        if (ModuleHandler.enabled(Woodcutters.class))
            registerWoodCutterRecipes(registration, recipeManager);

        if (ModuleHandler.enabled(Kilns.class))
            registerKilnRecipes(registration, recipeManager);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (ModuleHandler.enabled(Woodcutters.class))
            registerWoodCutterCategory(registration);

        if (ModuleHandler.enabled(Kilns.class))
            registerKilnCategory(registration);
    }

    private void registerWoodCutterCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WoodCuttingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private void registerKilnCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FiringRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    private void registerWoodCutterRecipes(IRecipeRegistration registration, RecipeManager recipeManager) {
        registration.addRecipes(recipeManager.getRecipesForType(Woodcutters.RECIPE_TYPE), WoodCuttingRecipeCategory.UID);
    }

    private void registerKilnRecipes(IRecipeRegistration registration, RecipeManager recipeManager) {
        registration.addRecipes(recipeManager.getRecipesForType(Kilns.RECIPE_TYPE), FiringRecipeCategory.UID);
    }

    private void registerReduceRepairCost(IRecipeRegistration registration, IVanillaRecipeFactory factory) {
        List<Object> recipes = new ArrayList<>();
        ItemStack damagedPick = new ItemStack(Items.DIAMOND_PICKAXE);
        damagedPick.setDamage(1000);

        ItemStack repairedPick = damagedPick.copy();
        repairedPick.setDamage(1000);

        recipes.add(factory.createAnvilRecipe(damagedPick,
                Collections.singletonList(new ItemStack(NetheriteNuggets.NETHERITE_NUGGET)),
                Collections.singletonList(repairedPick)
        ));

        registration.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
    }

}

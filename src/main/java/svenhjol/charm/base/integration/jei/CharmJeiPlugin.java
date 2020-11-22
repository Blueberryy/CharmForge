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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.DecreaseRepairCost;
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

        if (ModuleHandler.enabled(DecreaseRepairCost.class))
            registerReduceRepairCost(registration, factory);

        if(ModuleHandler.enabled(Woodcutters.class))
            registerWoodCutterRecipes(registration);
    }

    private void registerWoodCutterRecipes(IRecipeRegistration registration) {
        World world = Minecraft.getInstance().world;
        registration.addRecipes(world.getRecipeManager().getRecipesForType(Woodcutters.RECIPE_TYPE), WoodCuttingRecipeCategory.UID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if(ModuleHandler.enabled(Woodcutters.class))
            registerWoodCutterCategory(registration);
    }

    private void registerWoodCutterCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WoodCuttingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
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

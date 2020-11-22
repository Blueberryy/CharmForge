package svenhjol.charm.base.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.module.DecreaseRepairCost;
import svenhjol.charm.module.ExtractEnchantments;
import svenhjol.charm.module.NetheriteNuggets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        /*
        if (ModuleHandler.enabled(ExtractEnchantments.class))
            registerExtractEnchantments(registration, factory);*/
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

    private void registerExtractEnchantments(IRecipeRegistration registration, IVanillaRecipeFactory factory) {
        List<Object> recipes = new ArrayList<>();
        Enchantment enchant = Enchantments.UNBREAKING;

        ItemStack pick1 = new ItemStack(Items.DIAMOND_PICKAXE);
        ItemStack book1 = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {{ put(enchant, 2); }}, pick1);
        EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {{ put(enchant, 1); }}, book1);

        ItemStack pick2 = new ItemStack(Items.DIAMOND_PICKAXE);
        ItemStack book2 = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {{ put(enchant, 1); }}, pick2);
        EnchantmentHelper.setEnchantments(new HashMap<Enchantment, Integer>() {{ put(enchant, 1); }}, book2);

        recipes.add(factory.createAnvilRecipe(pick1,
                Collections.singletonList(new ItemStack(Items.BOOK)),
                Collections.singletonList(book1)
        ));

        recipes.add(factory.createAnvilRecipe(pick2,
                Collections.singletonList(new ItemStack(Items.BOOK)),
                Collections.singletonList(book2)
        ));

        registration.addRecipes(recipes, VanillaRecipeCategoryUid.ANVIL);
    }
}

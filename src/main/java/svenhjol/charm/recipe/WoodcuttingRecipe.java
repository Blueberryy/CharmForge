package svenhjol.charm.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.module.Woodcutters;

public class WoodcuttingRecipe extends SingleItemRecipe {
   public WoodcuttingRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output) {
      super(Woodcutters.RECIPE_TYPE, Woodcutters.RECIPE_SERIALIZER, id, group, input, output);
   }

   public boolean matches(IInventory inv, World world) {
      return this.ingredient.test(inv.getStackInSlot(0));
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getRecipeKindIcon() {
      return new ItemStack(Woodcutters.WOODCUTTER);
   }
}

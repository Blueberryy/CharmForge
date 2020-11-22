package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IIntArray;
import svenhjol.charm.module.Kilns;

public class KilnContainer extends AbstractFurnaceContainer {
    public KilnContainer(int syncId, PlayerInventory playerInventory) {
        super(Kilns.CONTAINER, Kilns.RECIPE_TYPE, RecipeBookCategory.SMOKER, syncId, playerInventory);
    }

    public KilnContainer(int syncId, PlayerInventory playerInventory, IInventory inventory, IIntArray propertyDelegate) {
        super(Kilns.CONTAINER, Kilns.RECIPE_TYPE, RecipeBookCategory.SMOKER, syncId, playerInventory, inventory, propertyDelegate);
    }
}

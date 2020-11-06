package svenhjol.charm.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import svenhjol.charm.module.Bookcases;

public class BookcaseSlot extends Slot {
    public BookcaseSlot(IInventory inventory, int slotIndex, int x, int y) {
        super(inventory, slotIndex, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return Bookcases.canContainItem(stack);
    }
}

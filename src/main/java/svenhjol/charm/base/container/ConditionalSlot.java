package svenhjol.charm.base.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

/**
 * @author Lukas
 * @since 28.12.2020
 */
public class ConditionalSlot extends Slot {
    private final Predicate<ItemStack> condition;

    public ConditionalSlot(Predicate<ItemStack> condition, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return condition.test(stack);
    }
}

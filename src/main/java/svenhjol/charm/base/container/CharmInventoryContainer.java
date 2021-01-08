package svenhjol.charm.base.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class CharmInventoryContainer extends CharmContainer {

    public CharmInventoryContainer(int rows, Predicate<ItemStack> condition, ContainerType<? extends CharmInventoryContainer> type, int syncId,
                                   PlayerInventory player, IInventory inventory) {
        super(type, syncId, player, inventory);

        // container's inventory slots
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new ConditionalSlot(condition, inventory, r * 9 + c, 8 + c * 18, 18 + r * 18));
            }
        }

        // player's main inventory slots
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(player, 9 + r * 9 + c, 8 + c * 18, 32 + (rows + r) * 18));
            }
        }

        // player's hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player, i, 8 + i * 18, 90 + rows * 18));
        }
    }

}

package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import svenhjol.charm.base.container.CharmContainer;
import svenhjol.charm.base.container.ConditionalSlot;
import svenhjol.charm.module.Atlas;

public class AtlasContainer extends CharmContainer {
    private final AtlasInventory inventory;

    public AtlasContainer(int syncId, PlayerInventory player, AtlasInventory inventory) {
        super(Atlas.CONTAINER, syncId, player, inventory);
        this.inventory = inventory;
        // container's inventory slots
        for (int r = 0; r < 3; ++r) {
            this.addSlot(new ConditionalSlot(stack -> stack.getItem() == Items.MAP, inventory, r, 8, 18 + r * 18));

        }

        // player's main inventory slots
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(player, 9 + r * 9 + c, 8 + c * 18, 84 + r * 18));
            }
        }

        // player's hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player, i, 8 + i * 18, 142));
        }
    }

    public AtlasInventory getAtlasInventory() {
        return inventory;
    }
}

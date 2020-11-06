package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import svenhjol.charm.base.screenhandler.CharmContainer;
import svenhjol.charm.module.Crates;
import svenhjol.charm.tileentity.CrateTileEntity;

public class CrateContainer extends CharmContainer {

    public CrateContainer(int syncId, PlayerInventory player) {
        this(syncId, player, new Inventory(CrateTileEntity.SIZE));
    }

    public CrateContainer(int syncId, PlayerInventory player, IInventory inventory) {
        super(Crates.CONTAINER, syncId, player, inventory);

        int index = 0;

        // container's inventory slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new CrateSlot(inventory, index++, 8 + (i * 18), 18));
        }

        index = 9;

        // player's main inventory slots
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(player, index++, 8 + c * 18, 50 + r * 18));
            }
        }

        // player's hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(player, i, 8 + (i * 18), 108));
        }
    }
}

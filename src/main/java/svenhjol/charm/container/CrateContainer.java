package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import svenhjol.charm.base.container.CharmInventoryContainer;
import svenhjol.charm.module.Crates;
import svenhjol.charm.tileentity.CrateTileEntity;

public class CrateContainer extends CharmInventoryContainer {

    public CrateContainer(int syncId, PlayerInventory player) {
        this(syncId, player, new Inventory(CrateTileEntity.SIZE));
    }

    public CrateContainer(int syncId, PlayerInventory player, IInventory inventory) {
        super(1, Crates::canCrateInsertItem, Crates.CONTAINER, syncId, player, inventory);
    }
}

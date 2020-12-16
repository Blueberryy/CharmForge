package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import svenhjol.charm.base.container.CharmInventoryContainer;
import svenhjol.charm.module.Atlas;

public class AtlasContainer extends CharmInventoryContainer {

    public AtlasContainer(int syncId, PlayerInventory player) {
        this(syncId, player, new Inventory(AtlasInventory.SIZE));
    }

    public AtlasContainer(int syncId, PlayerInventory player, IInventory inventory) {
        super(2, Atlas::canAtlasInsertItem, Atlas.CONTAINER, syncId, player, inventory);
    }
}

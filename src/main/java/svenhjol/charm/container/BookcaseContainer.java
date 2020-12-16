package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import svenhjol.charm.base.container.CharmInventoryContainer;
import svenhjol.charm.module.Bookcases;
import svenhjol.charm.tileentity.BookcaseTileEntity;

public class BookcaseContainer extends CharmInventoryContainer {
    public BookcaseContainer(int syncId, PlayerInventory player) {
        this(syncId, player, new Inventory(BookcaseTileEntity.SIZE));
    }

    public BookcaseContainer(int syncId, PlayerInventory player, IInventory inventory) {
        super(2, Bookcases::canContainItem, Bookcases.CONTAINER, syncId, player, inventory);
    }
}

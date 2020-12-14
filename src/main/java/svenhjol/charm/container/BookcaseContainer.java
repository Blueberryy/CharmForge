package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import svenhjol.charm.base.screenhandler.CharmContainer;
import svenhjol.charm.module.Bookcases;
import svenhjol.charm.tileentity.BookcaseTileEntity;

public class BookcaseContainer extends SimpleCharmContainer {
    public BookcaseContainer(int syncId, PlayerInventory player) {
        this(syncId, player, new Inventory(BookcaseTileEntity.SIZE));
    }

    public BookcaseContainer(int syncId, PlayerInventory player, IInventory inventory) {
        super(2, Bookcases::canContainItem, Bookcases.CONTAINER, syncId, player, inventory);
    }
}

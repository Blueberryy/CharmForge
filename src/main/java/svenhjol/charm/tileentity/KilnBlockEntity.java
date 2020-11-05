package svenhjol.charm.tileentity.;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.screenhandler.KilnScreenHandler;

public class KilnTileEntity extends AbstractFurnaceTileEntity {
    public KilnTileEntity() {
        super(Kilns.BLOCK_ENTITY, Kilns.RECIPE_TYPE);
    }

    @Override
    protected int getFuelTime(ItemStack fuel) {
        return super.getFuelTime(fuel) / 2;
    }

}

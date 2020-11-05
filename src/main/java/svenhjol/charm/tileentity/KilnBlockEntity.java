package svenhjol.charm.TileEntity;

import net.minecraft.block.entity.AbstractFurnaceTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.screenhandler.KilnScreenHandler;

public class KilnTileEntity extends AbstractFurnaceTileEntity {
    public KilnTileEntity() {
        super(Kilns.BLOCK_ENTITY, Kilns.RECIPE_TYPE);
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.charm.kiln");
    }

    @Override
    protected int getFuelTime(ItemStack fuel) {
        return super.getFuelTime(fuel) / 2;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new KilnScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}

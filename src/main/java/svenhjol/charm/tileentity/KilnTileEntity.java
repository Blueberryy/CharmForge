package svenhjol.charm.tileentity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.module.Kilns;
import svenhjol.charm.container.KilnContainer;

public class KilnTileEntity extends AbstractFurnaceTileEntity {
    public KilnTileEntity() {
        super(Kilns.BLOCK_ENTITY, Kilns.RECIPE_TYPE);
    }

    @Override
    protected int getBurnTime(ItemStack fuel) {
        return super.getBurnTime(fuel) / 2;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.charm.kiln");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new KilnContainer(id, player, this, this.furnaceData);
    }
}

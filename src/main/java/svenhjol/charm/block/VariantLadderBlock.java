package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;

public class VariantLadderBlock extends LadderBlock implements ICharmBlock {
    private final CharmModule module;

    public VariantLadderBlock(CharmModule module, IVariantMaterial type) {
        super(AbstractBlock.Properties.from(Blocks.LADDER));
        register(module, type.getString() + "_ladder");

        /** @see net.minecraft.block.entity.AbstractFurnaceTileEntity#createFuelTimeMap */
        this.setBurnTime(300);

        this.module = module;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        if (enabled())
            super.fillItemGroup(group, list);
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }
}

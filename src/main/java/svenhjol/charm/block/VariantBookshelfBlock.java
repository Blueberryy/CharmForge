package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;

public class VariantBookshelfBlock extends CharmBlock {
    public VariantBookshelfBlock(CharmModule module, IVariantMaterial type) {
        super(module, type.getString() + "_bookshelf", AbstractBlock.Properties.from(Blocks.BOOKSHELF));

        /** @see net.minecraft.block.FireBlock */
        if (type.isFlammable())
            this.setFireInfo(30, 20);

    }

    @Override
    public int getBurnTime() {
        return 300;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return 1;
    }
}

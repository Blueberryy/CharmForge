package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import svenhjol.charm.base.CharmModule;

public class GoldLanternBlock extends BaseLanternBlock {
    public GoldLanternBlock(CharmModule module, String name) {
        super(module, name, AbstractBlock.Properties.from(Blocks.LANTERN));
    }
}

package svenhjol.charm.base;

import net.minecraft.block.Block;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import svenhjol.charm.mixin.accessor.BlockTagsAccessor;

public class CharmTags {
    public static ITag.INamedTag<Block> BARRELS;
    public static ITag.INamedTag<Block> IMMOVABLE_BY_PISTONS;

    public static void init() {
        BARRELS = BlockTagsAccessor.invokeRegister("charm:barrels");
        IMMOVABLE_BY_PISTONS = BlockTagsAccessor.invokeRegister("charm:immovable_by_pistons");
    }
}

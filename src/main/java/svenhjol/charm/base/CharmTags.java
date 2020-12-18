package svenhjol.charm.base;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;

public class CharmTags {
    public static ITag.INamedTag<Block> BARRELS;
    public static ITag.INamedTag<Block> IMMOVABLE_BY_PISTONS;
    public static ITag.INamedTag<Block> NETHER_PORTAL_FRAMES;

    public static void init() {
        BARRELS = BlockTags.createOptional(new ResourceLocation(Charm.MOD_ID, "barrels"));
        IMMOVABLE_BY_PISTONS = BlockTags.createOptional(new ResourceLocation(Charm.MOD_ID, "immovable_by_pistons"));
        NETHER_PORTAL_FRAMES = BlockTags.createOptional(new ResourceLocation(Charm.MOD_ID, "nether_portal_frames"));
    }
}

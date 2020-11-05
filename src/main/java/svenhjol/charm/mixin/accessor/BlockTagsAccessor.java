package svenhjol.charm.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockTags.class)
public interface BlockTagsAccessor {
    @Invoker()
    static ITag.INamedTag<Block> invokeMakeWrapperTag(String id) {
        throw new IllegalStateException();
    }
}

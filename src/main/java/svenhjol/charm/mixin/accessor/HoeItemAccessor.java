package svenhjol.charm.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(HoeItem.class)
public interface HoeItemAccessor {
    @Accessor("EFFECTIVE_ON_BLOCKS")
    static Set<Block> getEffectiveOnBlocks() {
        throw new IllegalStateException();
    }
}

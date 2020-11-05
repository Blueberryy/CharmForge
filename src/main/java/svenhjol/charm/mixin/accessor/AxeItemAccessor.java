package svenhjol.charm.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
    @Accessor("EFFECTIVE_ON_BLOCKS")
    static Set<Block> getEffectiveOnBlocks() {
        throw new IllegalStateException();
    }
}

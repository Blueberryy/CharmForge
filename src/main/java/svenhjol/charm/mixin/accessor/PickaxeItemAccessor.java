package svenhjol.charm.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.item.PickaxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(PickaxeItem.class)
public interface PickaxeItemAccessor {
    @Accessor("EFFECTIVE_ON")
    static Set<Block> getEffectiveOn() {
        throw new IllegalStateException();
    }
}

package svenhjol.charm.mixin.accessor;

import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public interface DispenserBlockAccessor {
    /**
     * Used by OverrideHandler to access the dispenser behaviors.
     *
     * {@link svenhjol.charm.base.helper.OverrideHandler#changeDispenserBehavior(Item, Item)}
     */
    @Accessor("DISPENSE_BEHAVIOR_REGISTRY")
    static Map<Item, IDispenseItemBehavior> getDispenseBehaviorRegistry() {
        throw new IllegalStateException();
    }
}

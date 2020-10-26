package svenhjol.charm.mixin.accessor;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxBlockEntity.class)
public interface ShulkerBoxBlockEntityAccessor {
    @Accessor
    NonNullList<ItemStack> getInventory();
}

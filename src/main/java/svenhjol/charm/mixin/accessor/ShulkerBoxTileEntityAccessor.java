package svenhjol.charm.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShulkerBoxTileEntity.class)
public interface ShulkerBoxTileEntityAccessor {
    @Accessor
    NonNullList<ItemStack> getItems();
}

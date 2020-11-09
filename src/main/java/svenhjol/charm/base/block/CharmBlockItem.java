package svenhjol.charm.base.block;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class CharmBlockItem extends BlockItem {
    private int burnTime;
    private final BiConsumer<ItemStack, Boolean> inventoryTickConsumer;

    public CharmBlockItem(ICharmBlock block, Properties settings) {
        super((Block) block, settings);

        // set blockitem's registryname same as block's
        Block b = (Block)block;
        if (b.getRegistryName() != null)
            this.setRegistryName(b.getRegistryName());

        int burnTime = block.getBurnTime();
        if (burnTime > 0)
            this.burnTime = burnTime;

        // callable inventory tick consumer from the block
        this.inventoryTickConsumer = block.getInventoryTickConsumer();
    }

    @Override
    public int getBurnTime(ItemStack stack) {
        return burnTime;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (inventoryTickConsumer != null)
            inventoryTickConsumer.accept(stack, isSelected);
    }
}

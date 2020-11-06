package svenhjol.charm.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.mixin.accessor.*;

import java.util.function.BiConsumer;

public interface ICharmBlock {
    boolean enabled();

    default void register(CharmModule module, String name) {
        ResourceLocation id = new ResourceLocation(module.mod, name);
        RegistryHandler.block(id, (Block)this);
        createBlockItem(id);
    }

    default ItemGroup getItemGroup() {
        return ItemGroup.BUILDING_BLOCKS;
    }

    default int getBurnTime() { return 0; }

    default int getMaxStackSize() {
        return 64;
    }

    default void createBlockItem(ResourceLocation id) {
        Item.Properties settings = new Item.Properties();

        ItemGroup itemGroup = getItemGroup();
        if (itemGroup != null)
            settings.group(itemGroup);

        settings.maxStackSize(getMaxStackSize());

        CharmBlockItem blockItem = new CharmBlockItem(this, settings);
        RegistryHandler.item(id, blockItem);
    }

    default BiConsumer<ItemStack, Boolean> getInventoryTickConsumer() {
        return null;
    }

    default void setFireInfo(int encouragement, int flammability) {
        ((FireBlockAccessor) Blocks.FIRE).invokeSetFireInfo((Block)this, encouragement, flammability);
    }

    default void setEffectiveTool(Class<? extends ToolItem> clazz) {
        if (clazz == PickaxeItem.class)
            PickaxeItemAccessor.getEffectiveOn().add((Block)this);

        if (clazz == AxeItem.class)
            AxeItemAccessor.getEffectiveOnBlocks().add((Block)this);

        if (clazz == ShovelItem.class)
            ShovelItemAccessor.getEffectiveBlocks().add((Block)this);

        if (clazz == HoeItem.class)
            HoeItemAccessor.getEffectiveOnBlocks().add((Block)this);
    }
}

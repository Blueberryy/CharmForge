package svenhjol.charm.base.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.mixin.accessor.FireBlockAccessor;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ICharmBlock {
    boolean enabled();

    default void register(CharmModule module, String name) {
        ResourceLocation id = new ResourceLocation(module.mod, name);
        RegistryHandler.block(id, (Block)this);
        RegistryHandler.register(ForgeRegistries.ITEMS, () -> createBlockItem(id));
    }

    default ItemGroup getItemGroup() {
        return ItemGroup.BUILDING_BLOCKS;
    }

    default int getBurnTime() { return 0; }

    default int getMaxStackSize() {
        return 64;
    }

    @Nullable
    default BlockItem createBlockItem(ResourceLocation id) {
        Item.Properties settings = new Item.Properties();

        ItemGroup itemGroup = getItemGroup();
        if (itemGroup != null)
            settings.group(itemGroup);

        settings.maxStackSize(getMaxStackSize());

        // set item stack renderer function if present
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Supplier<Callable<ItemStackTileEntityRenderer>> ister = this.getISTER();

            if (ister != null)
                settings.setISTER(ister);
        });

        return new CharmBlockItem(this, settings);
    }

    default BiConsumer<ItemStack, Boolean> getInventoryTickConsumer() {
        return null;
    }

    default void setFireInfo(int encouragement, int flammability) {
        ((FireBlockAccessor) Blocks.FIRE).invokeSetFireInfo((Block)this, encouragement, flammability);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    default Supplier<Callable<ItemStackTileEntityRenderer>> getISTER() { return null; }

}

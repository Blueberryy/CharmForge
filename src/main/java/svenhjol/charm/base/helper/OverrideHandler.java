package svenhjol.charm.base.helper;

import com.mojang.serialization.Lifecycle;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import svenhjol.charm.mixin.accessor.BlockAccessor;
import svenhjol.charm.mixin.accessor.DispenserBlockAccessor;
import svenhjol.charm.mixin.accessor.ItemAccessor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unchecked", "rawtypes"})
public class OverrideHandler {
    private static final Map<Item, String> defaultItemKeys = new HashMap<>();
    private static final Map<Block, String> defaultBlockKeys = new HashMap<>();

    // TODO: overriding the registry in this way should not be done in Forge
    public static Item changeItem(ResourceLocation id, Item item) {
        int rawId = Registry.ITEM.getId(Registry.ITEM.getOrDefault(id));
        return (Item)((MutableRegistry)Registry.ITEM).register(rawId, RegistryKey.getOrCreateKey(Registry.ITEM.getRegistryKey(), id), item, Lifecycle.stable());
    }

    // TODO: overriding the registry in this way should not be done in Forge
    public static Block changeBlock(ResourceLocation id, Block block) {
        int rawId = Registry.BLOCK.getId(Registry.BLOCK.getOrDefault(id));
        return (Block)((MutableRegistry)Registry.BLOCK).register(rawId, RegistryKey.getOrCreateKey(Registry.BLOCK.getRegistryKey(), id), block, Lifecycle.stable());
    }

    public static void changeDispenserBehavior(Item existingItem, Item newItem) {
        IDispenseItemBehavior splashBehavior = DispenserBlockAccessor.getDispenseBehaviorRegistry().get(existingItem);
        DispenserBlock.registerDispenseBehavior(newItem, splashBehavior);
    }

    public static void changeItemTranslationKey(Item item, String newKey) {
        if (!defaultItemKeys.containsKey(item)) {
            // record the default before trying to set it
            defaultItemKeys.put(item, item.getTranslationKey());
        }

        if (newKey == null)
            newKey = defaultItemKeys.get(item);

        ((ItemAccessor)item).setTranslationKey(newKey);
    }

    public static void changeBlockTranslationKey(Block block, String newKey) {
        if (!defaultBlockKeys.containsKey(block)) {
            // record the default before trying to set it
            defaultBlockKeys.put(block, block.getTranslationKey());
        }

        if (newKey == null)
            newKey = defaultBlockKeys.get(block);

        ((BlockAccessor)block).setTranslationKey(newKey);
    }
}

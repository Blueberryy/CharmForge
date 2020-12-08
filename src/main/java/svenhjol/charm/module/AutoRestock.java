package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.CapabilityItemHandler;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@Module(mod = Charm.MOD_ID, description = "Refills your hotbar from your inventory.", hasSubscriptions = true)
public class AutoRestock extends CharmModule {
    //remember which items were in our hands and how often they were used
    private final Map<PlayerEntity, EnumMap<Hand, StackData>> handCache = new WeakHashMap<>();

    public static void addItemUsedStat(PlayerEntity player, ItemStack stack) {
        if (ModuleHandler.enabled(AutoRestock.class))
            player.addStat(Stats.ITEM_USED.get(stack.getItem()));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            ServerPlayerEntity player = (ServerPlayerEntity)event.player;
            EnumMap<Hand, StackData> cached = handCache.computeIfAbsent(player, it -> new EnumMap<>(Hand.class));
            for (Hand hand : Hand.values()) {
                StackData stackData = cached.get(hand);
                if (stackData != null && player.getHeldItem(hand).isEmpty() && getItemUsed(player, stackData.item) > stackData.used) {
                    findReplacement(player, hand, stackData);
                }
                updateCache(player, hand, cached);
            }
        }
    }

    private void updateCache(ServerPlayerEntity player, Hand hand, EnumMap<Hand, StackData> cached) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) {
            cached.put(hand, null);
        } else {
            Item item = stack.getItem();
            int used = getItemUsed(player, item);
            ListNBT enchantments = stack.getEnchantmentTagList();
            StackData stackData = cached.get(hand);
            if (stackData == null) {
                stackData = new StackData();
                cached.put(hand, stackData);
            }
            stackData.item = item;
            stackData.enchantments = enchantments;
            stackData.used = used;
        }
    }

    private int getItemUsed(ServerPlayerEntity player, Item item) {
        return player.getStats().getValue(Stats.ITEM_USED.get(item));
    }

    private void findReplacement(ServerPlayerEntity player, Hand hand, StackData stackData) {
        player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(inventory -> {
            //first 9 slots are the hotbar
            for (int i = 9; i < inventory.getSlots(); i++) {
                ItemStack possibleReplacement = inventory.extractItem(i, Integer.MAX_VALUE, true);
                if (stackData.item == possibleReplacement.getItem() && Objects.equals(stackData.enchantments, possibleReplacement.getEnchantmentTagList())) {
                    player.setHeldItem(hand, possibleReplacement.copy());
                    inventory.extractItem(i, Integer.MAX_VALUE, false);
                    break;
                }
            }
        });
    }

    private static class StackData {
        private Item item;
        private ListNBT enchantments;
        private int used;
    }

}

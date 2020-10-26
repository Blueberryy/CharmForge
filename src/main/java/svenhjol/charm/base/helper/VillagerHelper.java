package svenhjol.charm.base.helper;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import svenhjol.charm.base.handler.RegistryHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class VillagerHelper {
    public static VillagerProfession addProfession(ResourceLocation id, PointOfInterestType poit, SoundEvent worksound) {
        VillagerProfession profession = VillagerProfessionAccessor.create(id.toString(), poit, ImmutableSet.of(), ImmutableSet.of(), worksound);
        VillagerProfession registeredProfession = RegistryHandler.villagerProfession(id, profession);
        PROFESSION_TO_LEVELED_TRADE.put(profession, new Int2ObjectOpenHashMap<>());
        return registeredProfession;
    }

    public static PointOfInterestType addPointOfInterestType(ResourceLocation id, Block block, int ticketCount) {
        PointOfInterestType poit = PointOfInterestTypeAccessor.callCreate(id.toString(), ImmutableSet.copyOf(block.getStateManager().getStates()), ticketCount, 1);
        RegistryHandler.pointOfInterestType(id, poit);
        return PointOfInterestTypeAccessor.callSetup(poit);
    }

    public static void addTrade(VillagerProfession profession, int level, Factory trade) {
        Int2ObjectMap<Factory[]> fixedTrades = PROFESSION_TO_LEVELED_TRADE.get(profession);
        Int2ObjectMap<List<Factory>> mutableTrades = new Int2ObjectOpenHashMap<>();

        for (int i = 1; i <= 5; i++) {
            mutableTrades.put(i, NonNullList.create());
        }

        fixedTrades.int2ObjectEntrySet().forEach(e -> {
            Arrays.stream(e.getValue()).forEach(a -> mutableTrades.get(e.getIntKey()).add(a));
        });

        mutableTrades.get(level).add(trade);

        Int2ObjectMap<Factory[]> mappedTrades = new Int2ObjectOpenHashMap<>();
        mutableTrades.int2ObjectEntrySet().forEach(e -> mappedTrades.put(e.getIntKey(), e.getValue().toArray(new Factory[0])));
        PROFESSION_TO_LEVELED_TRADE.put(profession, mappedTrades);
    }

    public static void addWanderingTrade(Factory trade, boolean isRare) {
        if (isRare) {
            List<Factory> rareTrades = NonNullList.create();
            rareTrades.addAll(Arrays.asList(WANDERING_TRADER_TRADES.get(2)));
            rareTrades.add(trade);
            WANDERING_TRADER_TRADES.put(2, rareTrades.toArray(new Factory[0]));
        } else {
            List<Factory> normalTrades = NonNullList.create();
            normalTrades.addAll(Arrays.asList(WANDERING_TRADER_TRADES.get(1)));
            normalTrades.add(trade);
            WANDERING_TRADER_TRADES.put(1, normalTrades.toArray(new Factory[0]));
        }
    }

    public static abstract class SingleItemTypeTrade implements Factory {
        protected ItemConvertible  in = Items.AIR;
        protected ItemConvertible out = Items.EMERALD;
        protected int inCount = 1;
        protected int outCount = 1;
        protected int maxUses = 20;
        protected int experience = 2;
        protected float multiplier = 0.05F;

        public void setInput(ItemConvertible item, int count) {
            this.in = item;
            this.inCount = count;
        }

        public void setOutput(ItemConvertible item, int count) {
            this.out = item;
            this.outCount = count;
        }

        @Nullable
        @Override
        public TradeOffer create(Entity entity, Random random) {
            ItemStack in1 = new ItemStack(in, inCount);
            ItemStack out1 = new ItemStack(out, outCount);
            return new TradeOffer(in1, out1, maxUses, experience, multiplier);
        }
    }
}

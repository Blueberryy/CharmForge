package svenhjol.charm.base.helper;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.mixin.accessor.PointOfInterestTypeAccessor;

import javax.annotation.Nullable;
import java.util.Random;

public class VillagerHelper {
    public static VillagerProfession addProfession(ResourceLocation id, PointOfInterestType poit, SoundEvent worksound) {
        VillagerProfession profession = new VillagerProfession(id.toString(), poit, ImmutableSet.of(), ImmutableSet.of(), worksound);
        VillagerProfession registeredProfession = RegistryHandler.villagerProfession(id, profession);
//        PROFESSION_TO_LEVELED_TRADE.put(profession, new Int2ObjectOpenHashMap<>());
        return registeredProfession;
    }

    public static PointOfInterestType addPointOfInterestType(ResourceLocation id, Block block, int ticketCount) {
        PointOfInterestType poit = new PointOfInterestType(id.toString(), ImmutableSet.copyOf(block.getStateContainer().getValidStates()), ticketCount, 1);
        RegistryHandler.pointOfInterestType(id, poit);
        return PointOfInterestTypeAccessor.invokeRegisterBlockStates(poit);
    }

    public static abstract class SingleItemTypeTrade implements VillagerTrades.ITrade {
        protected IItemProvider in = Items.AIR;
        protected IItemProvider out = Items.EMERALD;
        protected int inCount = 1;
        protected int outCount = 1;
        protected int maxUses = 20;
        protected int experience = 2;
        protected float multiplier = 0.05F;

        public void setInput(IItemProvider item, int count) {
            this.in = item;
            this.inCount = count;
        }

        public void setOutput(IItemProvider item, int count) {
            this.out = item;
            this.outCount = count;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            ItemStack in1 = new ItemStack(in, inCount);
            ItemStack out1 = new ItemStack(out, outCount);
            return new MerchantOffer(in1, out1, maxUses, experience, multiplier);
        }
    }
}

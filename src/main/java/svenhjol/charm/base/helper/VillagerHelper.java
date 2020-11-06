package svenhjol.charm.base.helper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.IItemProvider;

import javax.annotation.Nullable;
import java.util.Random;

public class VillagerHelper {
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

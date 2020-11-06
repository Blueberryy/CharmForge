package svenhjol.charm.village;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.VillagerHelper.SingleItemTypeTrade;
import svenhjol.charm.module.Candles;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BeekeeperTradeOffers {
    public static class EmeraldsForFlowers extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            List<Item> flowers = ItemTags.FLOWERS.getAllElements();
            setInput(flowers.get(random.nextInt(flowers.size())), random.nextInt(3) + 13);
            setOutput(Items.EMERALD, 1);
            return super.getOffer(entity, random);
        }
    }

    public static class EmeraldsForCharcoal extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.CHARCOAL, random.nextInt(3) + 13);
            setOutput(Items.EMERALD, 1);
            return super.getOffer(entity, random);
        }
    }

    public static class EmeraldsForHoneycomb extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.HONEYCOMB, 10);
            setOutput(Items.EMERALD, random.nextInt(2) + 1);
            return super.getOffer(entity, random);
        }
    }

    public static class BottlesForEmerald extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.EMERALD, 1);
            setOutput(Items.GLASS_BOTTLE, random.nextInt(4) + 2);
            return super.getOffer(entity, random);
        }
    }

    public static class BeeswaxForEmeralds extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.EMERALD, 3);

            if (ModuleHandler.enabled("charm:candles")) {
                setOutput(Candles.BEESWAX, 1);
            } else {
                setOutput(Items.HONEYCOMB, 1);
            }

            return super.getOffer(entity, random);
        }
    }

    public static class CampfireForEmerald extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.EMERALD, 1);
            setOutput(Items.CAMPFIRE, 1);
            return super.getOffer(entity, random);
        }
    }

    public static class LeadForEmeralds extends SingleItemTypeTrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            setInput(Items.EMERALD, 6);
            setOutput(Items.LEAD, 1);
            return super.getOffer(entity, random);
        }
    }

    public static class HoneyBottlesForEmeralds implements VillagerTrades.ITrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            int count = random.nextInt(2) + 1;
            ItemStack in1 = new ItemStack(Items.EMERALD, count);
            ItemStack out = new ItemStack(Items.HONEY_BOTTLE, count);
            return new MerchantOffer(in1, out, 20, 2, 0.05F);
        }
    }

    public static class PopulatedBeehiveForEmeralds implements VillagerTrades.ITrade {
        @Nullable
        @Override
        public MerchantOffer getOffer(Entity entity, Random random) {
            int count = random.nextInt(14) + 21;
            ItemStack in1 = new ItemStack(Items.EMERALD, count);
            ItemStack out = new ItemStack(Items.BEEHIVE);

            BeehiveTileEntity tileEntity = new BeehiveTileEntity();

            for (int i = 0; i < 1; i++) {
                BeeEntity bee = new BeeEntity(EntityType.BEE, entity.world);
                tileEntity.tryEnterHive(bee, false, 0);
            }

            CompoundNBT beesTag = new CompoundNBT();
            CompoundNBT honeyTag = new CompoundNBT();
            beesTag.put("Bees", tileEntity.getBees());
            honeyTag.putInt("honey_level", 0);
            out.setTagInfo("TileEntityTag", beesTag);
            out.setTagInfo("BlockStateTag", honeyTag);
            out.setDisplayName(new TranslationTextComponent("item.charm.populated_beehive"));

            return new MerchantOffer(in1, out, 1, 10, 0.2F);
        }
    }
}

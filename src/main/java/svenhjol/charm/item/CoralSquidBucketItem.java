package svenhjol.charm.item;

import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.item.ICharmItem;
import svenhjol.charm.entity.CoralSquidEntity;
import svenhjol.charm.module.CoralSquids;

import javax.annotation.Nullable;

public class CoralSquidBucketItem extends BucketItem implements ICharmItem {
    public static final String STORED_CORAL_SQUID = "stored_coral_squid";

    private CharmModule module;

    public CoralSquidBucketItem(CharmModule module) {
        super(Fluids.WATER, new Item.Properties()
            .group(ItemGroup.MISC)
            .maxStackSize(1));

        this.module = module;
        register(module, "coral_squid_bucket");
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }

    @Override
    public void onLiquidPlaced(World world, ItemStack stack, BlockPos pos) {
        if (world instanceof ServerWorld) {
            CoralSquidEntity coralSquid = MobHelper.spawn(CoralSquids.CORAL_SQUID, (ServerWorld) world, pos, SpawnReason.BUCKET);
            if (coralSquid != null) {
                CompoundNBT data = ItemNBTHelper.getCompound(stack, STORED_CORAL_SQUID);
                if (!data.isEmpty())
                    coralSquid.readAdditional(data);

                if (stack.hasDisplayName())
                    coralSquid.setCustomName(stack.getDisplayName());

                world.addEntity(coralSquid);
            }
        }
    }

    @Override
    protected void playEmptySound(@Nullable PlayerEntity player, IWorld world, BlockPos pos) {
        world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }
}

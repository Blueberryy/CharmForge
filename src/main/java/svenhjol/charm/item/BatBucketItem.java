package svenhjol.charm.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.item.CharmItem;
import svenhjol.charm.message.ClientSetGlowingEntities;
import svenhjol.charm.module.BatBuckets;

public class BatBucketItem extends CharmItem {
    public static final String STORED_BAT = "stored_bat";

    public BatBucketItem(CharmModule module) {
        super(module, "bat_bucket", new Item.Properties()
            .group(ItemGroup.MISC)
            .maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (context.getPlayer() == null || context.getWorld().isRemote)
            return ActionResultType.FAIL;

        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getPos();
        Direction facing = context.getFace();
        Hand hand = context.getHand();
        ItemStack held = player.getHeldItem(hand);

        world.playSound(null, player.getPosition(), SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 1.0F, 1.0F);

        if (!world.isRemote && !player.isCreative()) {
            double x = pos.getX() + 0.5F + facing.getXOffset();
            double y = pos.getY() + 0.25F + (world.rand.nextFloat() / 2.0F) + facing.getYOffset();
            double z = pos.getZ() + 0.5F + facing.getZOffset();
            BlockPos spawnPos = new BlockPos(x, y, z);

            // spawn the bat
            BatEntity bat = MobHelper.spawn(EntityType.BAT, (ServerWorld)world, spawnPos, SpawnReason.BUCKET);
            if (bat != null) {

                CompoundNBT data = ItemNBTHelper.getCompound(held, STORED_BAT);
                if (!data.isEmpty())
                    bat.readAdditional(data);

                world.addEntity(bat);

                // damage the bat :(
                float health = bat.getHealth();
                bat.setHealth(health - 1.0F);
            }
        }
        player.swingArm(hand);

        // send message to client to start glowing
        if (!world.isRemote)
            Charm.PACKET_HANDLER.sendToPlayer(new ClientSetGlowingEntities(BatBuckets.glowingRange, BatBuckets.glowingTime * 20), (ServerPlayerEntity) player);

        if (!player.isCreative())
            player.setHeldItem(hand, new ItemStack(Items.BUCKET));

        return ActionResultType.SUCCESS;
    }
}

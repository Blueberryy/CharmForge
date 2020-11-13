package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.base.helper.PlayerHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.BatBucketsClient;
import svenhjol.charm.item.BatBucketItem;

@Module(mod = Charm.MOD_ID, client = BatBucketsClient.class, description = "Right-click a bat with a bucket to capture it. Right-click again to release it and locate entities around you.", hasSubscriptions = true)
public class BatBuckets extends CharmModule {
    public static BatBucketItem BAT_BUCKET_ITEM;

    @Config(name = "Glowing time", description = "Number of seconds that entities will receive the glowing effect.")
    public static int glowingTime = 10;

    @Config(name = "Viewing range", description = "Range (in blocks) in which entities will glow.")
    public static int glowingRange = 24;

    @Override
    public void register() {
        BAT_BUCKET_ITEM = new BatBucketItem(this);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.isCanceled())
            tryCapture(event.getPlayer(), event.getWorld(), event.getHand(), event.getEntity());
    }

    private void tryCapture(PlayerEntity player, World world, Hand hand, Entity entity) {
        if (!entity.world.isRemote
            && entity instanceof BatEntity
            && ((BatEntity)entity).getHealth() > 0
        ) {
            BatEntity bat = (BatEntity)entity;
            ItemStack held = player.getHeldItem(hand);

            if (held.isEmpty() || held.getItem() != Items.BUCKET)
                return;

            ItemStack batBucket = new ItemStack(BAT_BUCKET_ITEM);
            CompoundNBT tag = bat.serializeNBT();
            ItemNBTHelper.setCompound(batBucket, BatBucketItem.STORED_BAT, tag);

            if (held.getCount() == 1) {
                player.setHeldItem(hand, batBucket);
            } else {
                held.shrink(1);
                PlayerHelper.addOrDropStack(player, batBucket);
            }

            player.swingArm(hand);
            entity.remove();
        }
    }
}

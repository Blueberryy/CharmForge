package svenhjol.charm.module;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.item.RaidHornItem;

@Module(mod = Charm.MOD_ID, hasSubscriptions = true, description = "Raid horns are sometimes dropped from raid leaders and can be used to call off or start raids.")
public class RaidHorns extends CharmModule {
    public static RaidHornItem RAID_HORN;

    public static double lootingBoost = 0.25D;

    @Config(name = "Drop chance", description = "Chance (out of 1.0) of a patrol captain dropping a raid horn when killed by the player.")
    public static double dropChance = 0.05D;

    @Config(name = "Volume", description = "Volume of the raid horn sound effect when used.  1.0 is maximum volume.")
    public static double volume = 0.75D;

    @Override
    public void register() {
        RAID_HORN = new RaidHornItem(this);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.isCanceled())
            tryDrop(event.getEntityLiving(), event.getSource(), event.getLootingLevel());
    }


    public void tryDrop(LivingEntity entity, DamageSource source, int lootingLevel) {
        if (!entity.world.isRemote
            && entity instanceof PatrollerEntity
            && source.getTrueSource() instanceof PlayerEntity
            && entity.world.rand.nextFloat() <= (dropChance + lootingBoost * lootingLevel)
        ) {
            if (((PatrollerEntity)entity).isLeader()) {
                BlockPos pos = entity.getPosition();
                ItemStack potion = new ItemStack(RAID_HORN);
                entity.world.addEntity(new ItemEntity(entity.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), potion));
            }
        }
    }
}

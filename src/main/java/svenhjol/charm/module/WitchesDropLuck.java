package svenhjol.charm.module;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.PotionHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "A witch has a chance to drop a Potion of Luck when killed by a player.", hasSubscriptions = true)
public class WitchesDropLuck extends CharmModule {
    public static double lootingBoost = 0.25D;

    @Config(name = "Drop chance", description = "Chance (out of 1.0) of a witch dropping a Potion of Luck when killed by the player.")
    public static double dropChance = 0.05D;

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.isCanceled())
            tryDrop(event.getEntityLiving(), event.getSource(), event.getLootingLevel());
    }

    public void tryDrop(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
        if (!entity.world.isRemote
            && entity instanceof WitchEntity
            && damageSource.getTrueSource() instanceof PlayerEntity
            && entity.world.rand.nextFloat() <= (dropChance + lootingBoost * lootingLevel)
        ) {
            BlockPos pos = entity.getPosition();
            ItemStack potion = PotionHelper.getPotionItemStack(Potions.LUCK, 1);
            entity.world.addEntity(new ItemEntity(entity.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), potion));
        }
    }
}

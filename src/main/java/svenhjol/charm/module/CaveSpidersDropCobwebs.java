package svenhjol.charm.module;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ItemHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Cave spiders have a chance to drop cobwebs.", hasSubscriptions = true)
public class CaveSpidersDropCobwebs extends CharmModule {
    public static double lootingBoost = 0.3D;

    @Config(name = "Maximum drops", description = "Maximum cobwebs dropped when cave spider is killed.")
    public static int maxDrops = 2;

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.isCanceled())
            tryDropCobweb(event.getEntityLiving(), event.getSource(), event.getLootingLevel());
    }

    public void tryDropCobweb(LivingEntity entity, DamageSource source, int lootingLevel) {
        if (!entity.world.isRemote
            && entity instanceof CaveSpiderEntity
        ) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getPosition();

            int amount = ItemHelper.getAmountWithLooting(world.rand, maxDrops, lootingLevel, (float)lootingBoost);
            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.COBWEB, amount)));
        }
    }
}

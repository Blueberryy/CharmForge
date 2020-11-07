package svenhjol.charm.module;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ItemHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Husks spawn anywhere within their biome and have a chance to drop sand.")
public class HuskImprovements extends CharmModule {
    public static double lootingBoost = 0.3D;

    @Config(name = "Spawn anywhere in biome", description = "If true, husks can spawn anywhere within their biome rather than just the surface.")
    public static boolean spawnAnywhere = true;

    @Config(name = "Drop sand when killed", description = "If true, husks drop sand when killed.")
    public static boolean dropSand = true;

    @Config(name = "Maximum drops", description = "Maximum sand dropped when husk is killed.")
    public static int maxDrops = 2;

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.isCanceled())
            tryDrop(event.getEntityLiving(), event.getSource(), event.getLootingLevel());
    }

    public static boolean canSpawn() {
        return ModuleHandler.enabled("charm:husk_improvements") && spawnAnywhere;
    }

    private void tryDrop(Entity entity, DamageSource source, int lootingLevel) {
        if (dropSand
            && !entity.world.isRemote
            && entity instanceof HuskEntity
        ) {
            World world = entity.getEntityWorld();
            BlockPos pos = entity.getPosition();
            int amount = ItemHelper.getAmountWithLooting(world.rand, maxDrops, lootingLevel, (float)lootingBoost);
            world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.SAND, amount)));
        }
    }
}

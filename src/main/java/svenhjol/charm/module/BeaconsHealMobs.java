package svenhjol.charm.module;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;

import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Passive and friendly mobs will heal themselves within range of a beacon with the regeneration effect.")
public class BeaconsHealMobs extends CharmModule {
    public static void healInBeaconRange(World world, int levels, BlockPos pos, Effect primaryEffect, Effect secondaryEffect) {
        if (!ModuleHandler.enabled(BeaconsHealMobs.class))
            return;

        if (!world.isRemote) {
            double d0 = levels * 10 + 10;
            AxisAlignedBB bb = (new AxisAlignedBB(pos)).grow(d0).expand(0.0D, world.getHeight(), 0.0D);

            if (primaryEffect == Effects.REGENERATION || secondaryEffect == Effects.REGENERATION) {
                List<AgeableEntity> list = world.getEntitiesWithinAABB(AgeableEntity.class, bb);
                list.forEach(mob -> mob.addPotionEffect(new EffectInstance(Effects.REGENERATION, 4 * 20, 1)));
            }
        }
    }
}

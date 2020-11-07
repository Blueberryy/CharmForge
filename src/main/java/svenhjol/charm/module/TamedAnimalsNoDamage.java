package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Tamed animals do not take direct damage from players.", hasSubscriptions = true)
public class TamedAnimalsNoDamage extends CharmModule {

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (!event.isCanceled()) {
            boolean result = tryIgnoreAttack(event.getPlayer(), event.getEntity().getEntityWorld(), event.getTarget());
            if (result)
                event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!event.isCanceled()) {
            boolean result = tryIgnoreDamage(event.getEntityLiving(), event.getSource(), event.getAmount());
            if (result)
                event.setCanceled(true);
        }
    }

    private boolean tryIgnoreAttack(PlayerEntity player, World world, Entity entity) {
        if (entity instanceof TameableEntity
            && ((TameableEntity)entity).isTamed()
            && !player.isCreative()
        ) {
            return true;
        }

        return false;
    }

    private boolean tryIgnoreDamage(LivingEntity entity, DamageSource damageSource, float amount) {
        if (!(entity instanceof PlayerEntity)) {
            Entity attacker = damageSource.getImmediateSource();
            Entity source = damageSource.getTrueSource();

            PlayerEntity player = null;

            if (source instanceof PlayerEntity) player = (PlayerEntity) source;
            if (attacker instanceof PlayerEntity) player = (PlayerEntity) attacker;

            if (player != null && !player.isCreative())
                if (entity instanceof TameableEntity && ((TameableEntity) entity).isTamed())
                    return true;
        }

        return false;
    }
}

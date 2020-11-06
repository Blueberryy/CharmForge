package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.EnchantmentsHelper;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "A player wearing feather falling enchanted boots will not trample crops.", hasSubscriptions = true)
public class FeatherFallingCrops extends CharmModule {
    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (!event.isCanceled()) {
            boolean result = landedOnFarmlandBlock(event.getEntity());
            if (result)
                event.setCanceled(true);
        }
    }

    public boolean landedOnFarmlandBlock(Entity entity) {
        return ModuleHandler.enabled("charm:feather_falling_crops")
            && entity instanceof LivingEntity
            && EnchantmentsHelper.hasFeatherFalling((LivingEntity)entity);
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.EnchantmentsHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "A player wearing feather falling enchanted boots will not trample crops.", hasSubscriptions = true)
public class FeatherFallingCrops extends CharmModule {

    @Config(name = "Requires feather falling", description = "Turn this off to prevent trampling even when the player does not wear feather falling boots.")
    public static boolean requiresFeatherFalling = true;

    @SubscribeEvent
    public void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (!event.isCanceled()) {
            boolean result = landedOnFarmlandBlock(event.getEntity());
            if (result)
                event.setCanceled(true);
        }
    }

    public boolean landedOnFarmlandBlock(Entity entity) {
        return ModuleHandler.enabled(FeatherFallingCrops.class)
                && entity instanceof LivingEntity
                && (!requiresFeatherFalling || EnchantmentsHelper.hasFeatherFalling((LivingEntity) entity));
    }
}

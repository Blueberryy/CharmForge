package svenhjol.charm.module;

import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Chickens randomly drop feathers.")
public class ChickensDropFeathers extends CharmModule {
    public static void tryDropFeather(ChickenEntity chicken) {
        if (!ModuleHandler.enabled(ChickensDropFeathers.class))
            return;

        if (chicken.isAlive()
            && !chicken.isChild()
            && !chicken.world.isRemote
            && !chicken.isChickenJockey()
            && chicken.world.rand.nextFloat() < 0.2F
        ) {
            chicken.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (chicken.world.rand.nextFloat() - chicken.world.rand.nextFloat()) * 0.2F + 1.0F);
            chicken.entityDropItem(Items.FEATHER);
            chicken.timeUntilNextEgg = chicken.world.rand.nextInt(3000) + 3000;
        }
    }
}

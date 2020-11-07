package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "When any action would cause a villager to become a nitwit, it becomes an unemployed villager instead.", hasSubscriptions = true)
public class RemoveNitwits extends CharmModule {
    @SubscribeEvent
    public void onVillagerJoinWorld(EntityJoinWorldEvent event) {
        if (!event.isCanceled())
            changeNitwitProfession(event.getEntity());
    }

    private void changeNitwitProfession(Entity entity) {
        if (!entity.world.isRemote
            && entity instanceof VillagerEntity
        ) {
            VillagerEntity villager = (VillagerEntity) entity;
            VillagerData data = villager.getVillagerData();

            if (data.getProfession() == VillagerProfession.NITWIT) {
                villager.setVillagerData(data.withProfession(VillagerProfession.NONE));
                Charm.LOG.debug("Changed nitwit's profession to NONE: " + villager.getCachedUniqueIdString());
            }
        }
    }
}

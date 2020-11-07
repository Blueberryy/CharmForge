package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.util.ActionResult;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "When any action would cause a villager to become a nitwit, it becomes an unemployed villager instead.")
public class RemoveNitwits extends CharmModule {
    @Override
    public void init() {
        AddEntityCallback.EVENT.register(this::changeNitwitProfession);
    }

    private ActionResult changeNitwitProfession(Entity entity) {
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

        return ActionResult.PASS;
    }
}

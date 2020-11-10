package svenhjol.charm.module;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Villagers are attracted when the player holds a block of emeralds.", hasSubscriptions = true)
public class VillagersFollowEmeraldBlocks extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        depends(!ModHelper.isLoaded("quark") || override);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.isCanceled())
            followEmerald(event.getEntity());
    }

    private void followEmerald(Entity entity) {
        if (entity instanceof VillagerEntity) {
            VillagerEntity villager = (VillagerEntity) entity;

            Ingredient ingredient = Ingredient.fromItems(Blocks.EMERALD_BLOCK);

            if (MobHelper.getGoals(villager).stream().noneMatch(g -> g.getGoal() instanceof TemptGoal))
                MobHelper.getGoalSelector(villager).addGoal(3, new TemptGoal(villager, 0.6, ingredient, false));
        }
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

import java.util.Collection;

@Module(mod = Charm.MOD_ID, description = "Unlocks all vanilla recipes.", hasSubscriptions = true)
public class AutomaticRecipeUnlock extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        depends(!ModHelper.isLoaded("quark") || override);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        unlockRecipes(event.getPlayer());
    }

    public static void unlockRecipes(PlayerEntity player) {
        if (!ModuleHandler.enabled("charm:automatic_recipe_unlock"))
            return;

        if (player != null) {
            RecipeManager recipeManager = player.world.getRecipeManager();
            Collection<IRecipe<?>> allRecipes = recipeManager.getRecipes();
            player.unlockRecipes(allRecipes);
        }
    }
}

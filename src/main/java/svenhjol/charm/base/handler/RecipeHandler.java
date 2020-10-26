package svenhjol.charm.base.handler;

import com.google.gson.JsonElement;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.StringHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RecipeHandler {
    public static void filter(Map<ResourceLocation, JsonElement> recipes) {
        Map<String, CharmModule> loadedModules = ModuleHandler.getLoadedModules();

        for (String modId : loadedModules.keySet()) {

            // remove recipes specified by enabled modules
            loadedModules.values().stream().filter(m -> m.enabled && !m.getRecipesToRemove().isEmpty()).forEach(m -> {
                m.getRecipesToRemove().forEach(recipes::remove);
            });

            // fetch all the recipes that match the mod's ID
            List<ResourceLocation> modRecipes = recipes.keySet().stream().filter(r -> r.getNamespace().equals(modId)).collect(Collectors.toList());

            modRecipes.forEach(recipeId -> {
                String path = recipeId.getPath();
                if (!path.contains("/"))
                    return;

                String moduleId = StringHelper.snakeToUpperCamel(path.split("/")[0]);

                // remove recipes for disabled modules
                if (loadedModules.containsKey(moduleId) && !loadedModules.get(moduleId).enabled)
                    recipes.remove(recipeId);
            });
        }
    }

    public static Iterator<Map.Entry<ResourceLocation, JsonElement>> sortedRecipes(Map<ResourceLocation, JsonElement> recipes) {
        return Stream.concat(
            recipes.entrySet().stream().filter(r -> !r.getKey().getNamespace().equals("minecraft")),
            recipes.entrySet().stream().filter(r -> r.getKey().getNamespace().equals("minecraft"))
        ).iterator();
    }
}

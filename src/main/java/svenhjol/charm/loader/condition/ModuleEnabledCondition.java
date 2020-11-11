package svenhjol.charm.loader.condition;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;

public class ModuleEnabledCondition implements ICondition {
    private final String moduleName;

    public ModuleEnabledCondition(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(Charm.MOD_ID.toLowerCase(), "module_enabled");
    }

    @Override
    public boolean test() {
        return ModuleHandler.enabled(moduleName);
    }

    public static class Serializer implements IConditionSerializer<ModuleEnabledCondition> {
        public Serializer() {
        }

        @Override
        public void write(JsonObject json, ModuleEnabledCondition value) {
            json.addProperty("module", value.moduleName);
        }

        @Override
        public ModuleEnabledCondition read(JsonObject json) {
            return new ModuleEnabledCondition(json.getAsJsonPrimitive("module").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return new ResourceLocation(Charm.MOD_ID.toLowerCase(), "module_enabled");
        }
    }
}

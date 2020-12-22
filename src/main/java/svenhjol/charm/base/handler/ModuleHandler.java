package svenhjol.charm.base.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmLoader;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.StringHelper;
import svenhjol.charm.base.loader.condition.ModuleEnabledCondition;
import svenhjol.charm.module.Quark;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleHandler {
    private static final Map<String, ModContainer> FORGE_MOD_CONTAINERS = new ConcurrentHashMap<>();
    private static List<Class<? extends CharmModule>> ENABLED_MODULES = new ArrayList<>(); // this is a cache of enabled classes

    public static ConfigHandler CONFIG_HANDLER;

    public static Map<String, CharmModule> LOADED_MODULES = new ConcurrentHashMap<>();
    public static final Map<String, CharmLoader> LOADER_INSTANCES = new HashMap<>();

    public static final IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

    public static ModuleHandler INSTANCE = new ModuleHandler();

    public ModuleHandler() {
        // register forge events
        MOD_EVENT_BUS.addListener(this::onCommonSetup);
        MOD_EVENT_BUS.addListener(this::onModConfig);
        FORGE_EVENT_BUS.addListener(this::onServerStarting);

        CONFIG_HANDLER = new ConfigHandler();
        BiomeHandler.init();
        CraftingHelper.register(new ModuleEnabledCondition.Serializer());
    }

    public void addLoader(CharmLoader loader) {
        String modId = loader.getModId();

        // create loader reference
        LOADER_INSTANCES.put(modId, loader);

        // subscribe the loader to forge events
        MOD_EVENT_BUS.addListener(loader::onCommonSetup);
        MOD_EVENT_BUS.addListener(loader::onModConfig);
        FORGE_EVENT_BUS.addListener(loader::onServerStarting);

        Charm.LOG.info("Subscribed '" + modId + "' to Forge event bus");
    }

    @Nullable
    public CharmLoader getLoader(String modId) {
        return LOADER_INSTANCES.getOrDefault(modId, null);
    }

    public void register(CharmModule module) {
        LOADED_MODULES.put(module.getName(), module);

        Charm.LOG.debug("Registering module " + module.getName());
        module.register();
    }

    public void depends(CharmModule module) {
        String name = module.getName();
        boolean isEnabled = module.enabled;
        boolean dependencyCheck = module.depends();

        String message;
        if (!isEnabled) {
            message = "Module " + name + " is not enabled.";
        } else if (!dependencyCheck) {
            message = "Module " + name + " did not pass dependency check, disabling.";
        } else {
            message = "Module " + name + " is enabled.";
        }

        Charm.LOG.debug("[ModuleHandler] " + message);
        module.enabled = isEnabled && dependencyCheck;
    }

    public void init(CharmModule module) {
        // this is a cache for quick lookup of enabled classes
        ModuleHandler.ENABLED_MODULES.add(module.getClass());

        // subscribe the module to the event bus if required
        if (module.hasSubscriptions)
            FORGE_EVENT_BUS.register(module);

        Charm.LOG.info("Initialising module " + module.getName());
        module.init();
    }

    public void onModConfig(ModConfig.ModConfigEvent event) {
        ConfigHandler.refreshAllConfig();
    }

    public void onCommonSetup(FMLCommonSetupEvent event) {

    }

    public void onServerStarting(FMLServerStartingEvent event) {
        DecorationHandler.init(); // load late so that tags are populated at this point
    }

    @Nullable
    public static CharmModule getModule(String moduleName) {
        return LOADED_MODULES.getOrDefault(StringHelper.snakeToUpperCamel(moduleName), null);
    }

    public static Map<String, CharmModule> getLoadedModules() {
        return LOADED_MODULES;
    }

    /**
     * Use this within static hook methods for quick check if a module is enabled.
     * @param clazz Module to check
     * @return True if the module is enabled
     */
    public static boolean enabled(Class<? extends CharmModule> clazz) {
        return ENABLED_MODULES.contains(clazz);
    }

    /**
     * Use this anywhere to check a module's enabled status for any Charm-based (or Quark) module.
     * @param moduleName Name (modid:module_name) of module to check
     * @return True if the module is enabled
     */
    public static boolean enabled(String moduleName) {
        String[] split = moduleName.split(":");
        String modName = split[0]; // TODO: check module is running
        String modModule = split[1];

        switch (modName) {
            case Charm.MOD_ID:
                CharmModule module = getModule(modModule);
                return module != null && module.enabled;
            case "quark":
                return Quark.compat.isModuleEnabled(modModule);
            default:
                return false;
        }
    }
}

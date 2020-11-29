package svenhjol.charm.base.handler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import svenhjol.charm.Charm;
import svenhjol.charm.CharmClient;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.StringHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.base.loader.condition.ModuleEnabledCondition;
import svenhjol.charm.module.Quark;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ModuleHandler {
    public static Map<String, List<Class<? extends CharmModule>>> AVAILABLE_MODULES = new HashMap<>();
    public static Map<String, CharmModule> LOADED_MODULES = new ConcurrentHashMap<>();
    private static final Map<String, ModContainer> FORGE_MOD_CONTAINERS = new ConcurrentHashMap<>();
    private static List<Class<? extends CharmModule>> ENABLED_MODULES = new ArrayList<>(); // this is a cache of enabled classes

    public static final IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

    private static boolean hasInit = false;

    public static void init() {
        if (hasInit)
            return;

        // register forge events
        MOD_EVENT_BUS.register(RegistryHandler.class);
        MOD_EVENT_BUS.addListener(ModuleHandler::onConstructMod);
        MOD_EVENT_BUS.addListener(ModuleHandler::onCommonSetup);
        MOD_EVENT_BUS.addListener(ModuleHandler::onModConfig);
        FORGE_EVENT_BUS.addListener(ModuleHandler::onServerStarting);

        // both-side initializers
        BiomeHandler.init();
        CraftingHelper.register(new ModuleEnabledCondition.Serializer());

        hasInit = true;
    }

    public static void registerForgeMod(String modId) {
        FORGE_MOD_CONTAINERS.put(modId, ModLoadingContext.get().getActiveContainer());
    }

    public static void onConstructMod(FMLConstructModEvent event) {
        // create all charm-based modules
        instantiateModules();

        // early init, always run, use for registering things
        eachModule(CharmModule::register);

        // run client things on the client thread
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CharmClient::new);
    }

    public static void onModConfig(ModConfig.ModConfigEvent event) {
        ConfigHandler.refreshAllConfig();

        eachEnabledModule(module -> module.enabled = module.depends());
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ENABLED_MODULES = new ArrayList<>();

        eachEnabledModule(module -> {
            if (module.hasSubscriptions)
                FORGE_EVENT_BUS.register(module);

            // this is a cache for quick lookup of enabled classes
            ENABLED_MODULES.add(module.getClass());
            module.init();
        });
    }

    public static void onServerStarting(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        DecorationHandler.init(); // load late so that tags are populated at this point
        eachEnabledModule(m -> m.loadWorld(server));
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

    private static void instantiateModules() {
        ConfigHandler configHandler = new ConfigHandler();

        AVAILABLE_MODULES.forEach((mod, modules) -> {
            if (!FORGE_MOD_CONTAINERS.containsKey(mod))
                throw new RuntimeException("You must register a charm-based forge mod using ModuleHandler.registerForgeMod.");

            ModContainer modContainer = FORGE_MOD_CONTAINERS.get(mod);
            Map<String, CharmModule> loaded = new TreeMap<>();

            modules.forEach(clazz -> {
                try {
                    CharmModule module = clazz.getDeclaredConstructor().newInstance();
                    if (clazz.isAnnotationPresent(Module.class)) {
                        Module annotation = clazz.getAnnotation(Module.class);

                        // mod is now a required string
                        if (annotation.mod().isEmpty())
                            throw new Exception("mod name must be defined");

                        module.mod = annotation.mod();
                        module.alwaysEnabled = annotation.alwaysEnabled();
                        module.enabledByDefault = annotation.enabledByDefault();
                        module.hasSubscriptions = annotation.hasSubscriptions();
                        module.enabled = module.enabledByDefault;
                        module.description = annotation.description();
                        module.client = annotation.client();

                        String moduleName = module.getName();
                        loaded.put(moduleName, module);

                    } else {
                        throw new RuntimeException("No module annotation for class " + clazz.toString());
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Could not initialize module class: " + clazz.toString(), e);
                }
            });

            // config for this module set
            configHandler.createConfig(modContainer, loaded);

            // add loaded modules
            loaded.forEach((moduleName, module) ->
                LOADED_MODULES.put(moduleName, module));

        });
    }

    private static void eachModule(Consumer<CharmModule> consumer) {
        LOADED_MODULES.values().forEach(consumer);
    }

    private static void eachEnabledModule(Consumer<CharmModule> consumer) {
        LOADED_MODULES.values()
            .stream()
            .filter(m -> m.enabled)
            .forEach(consumer);
    }
}

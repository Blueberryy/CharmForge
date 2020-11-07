package svenhjol.charm.base.handler;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.StringHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.event.StructureSetupEvent;
import svenhjol.charm.handler.ColoredGlintHandler;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ModuleHandler {
    public static Map<String, List<Class<? extends CharmModule>>> AVAILABLE_MODULES = new HashMap<>();
    public static Map<String, CharmModule> LOADED_MODULES = new ConcurrentHashMap<>();

    public static final IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static final IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

    private static boolean hasInit = false;

    public static void init() {
        if (hasInit) return;

        // register forge events
        MOD_EVENT_BUS.register(RegistryHandler.class);
        MOD_EVENT_BUS.addListener(ModuleHandler::onCommonSetup);
        MOD_EVENT_BUS.addListener(ModuleHandler::onModConfig);
        FORGE_EVENT_BUS.addListener(ModuleHandler::onServerStarting);

        // create all charm-based modules
        instantiateModules();

        // both-side initializers
        BiomeHandler.init();
        // TODO: forge module enabled conditions should register here

        // early init, always run, use for registering things
        eachModule(CharmModule::register);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eachModule(CharmModule::clientRegister);
            MOD_EVENT_BUS.addListener(ModuleHandler::onClientSetup);
        });

        /** @deprecated listen for server setup events (dedicated server only) */
        //DedicatedServerSetupCallback.EVENT.register(server -> {
        //    eachEnabledModule(m -> m.dedicatedServerInit(server));
        //});

        hasInit = true;
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        eachEnabledModule(module -> {
            if (module.hasSubscriptions)
                FORGE_EVENT_BUS.register(module);

            module.init();
        });

        // allow modules to modify structures via an event
        FORGE_EVENT_BUS.post(new StructureSetupEvent());
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        eachEnabledModule(CharmModule::clientInit);
    }

    public static void onModConfig(ModConfig.ModConfigEvent event) {

    }

    public static void onLoadComplete(FMLLoadCompleteEvent event) {
        DecorationHandler.init(); // load late so that tags are populated
        ColoredGlintHandler.init(); // load late so that buffer builders are populated
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

    public static boolean enabled(String moduleName) {
        String[] split = moduleName.split(":");
        String modName = split[0]; // TODO: check module is running
        String modModule = split[1];

        CharmModule module = getModule(modModule);
        return module != null && module.enabled;
    }

    public static boolean isClient() {
        throw new RuntimeException("Do not call this");
    }

    private static void instantiateModules() {
        AVAILABLE_MODULES.forEach((mod, modules) -> {
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
            ConfigHandler.createConfig(mod, loaded);

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

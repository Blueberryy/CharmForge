package svenhjol.charm.base;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class CharmLoader {
    private final String MOD_ID;
    private final List<Class<? extends CharmModule>> CLASSES;
    private final Map<String, CharmModule> LOADED_MODULES = new TreeMap<>();

    public CharmLoader(String modId, List<Class<? extends CharmModule>> classes) {
        MOD_ID = modId;
        CLASSES = classes;

        Charm.LOG.info("Setting up a new Charm-based module '" + modId + "'");

        register();
    }

    public String getModId() {
        return MOD_ID;
    }

    public List<Class<? extends CharmModule>> getClasses() {
        return CLASSES;
    }

    protected void register() {
        ModuleHandler.INSTANCE.addLoader(this);

        ModContainer modContainer = ModLoadingContext.get().getActiveContainer();

        CLASSES.forEach(clazz -> {
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
                    LOADED_MODULES.put(moduleName, module);

                } else {
                    throw new RuntimeException("No module annotation for class " + clazz.toString());
                }

            } catch (Exception e) {
                throw new RuntimeException("Could not initialize module class: " + clazz.toString(), e);
            }
        });

        // config for this module set
        ModuleHandler.CONFIG_HANDLER.createConfig(modContainer, LOADED_MODULES);

        // add and run register method for all loaded modules
        LOADED_MODULES.forEach((moduleName, module) -> ModuleHandler.INSTANCE.register(module));
    }

    public void onModConfig(ModConfig.ModConfigEvent event) {

    }

    public void onCommonSetup(FMLCommonSetupEvent event) {
        // always run onCommonSetup
        eachModule(module -> module.onCommonSetup(event));

        // run dependency check on each module
        eachModule(ModuleHandler.INSTANCE::depends);

        // post init, only enabled modules are run
        eachEnabledModule(ModuleHandler.INSTANCE::init);
    }

    public void onServerStarting(FMLServerStartingEvent event) {
        eachEnabledModule(m -> m.loadWorld(event.getServer()));
    }

    protected void eachModule(Consumer<CharmModule> consumer) {
        LOADED_MODULES.values().forEach(consumer);
    }

    protected void eachEnabledModule(Consumer<CharmModule> consumer) {
        LOADED_MODULES.values()
            .stream()
            .filter(m -> m.enabled)
            .forEach(consumer);
    }
}

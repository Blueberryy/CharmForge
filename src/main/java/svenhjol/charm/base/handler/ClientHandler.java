package svenhjol.charm.base.handler;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import svenhjol.charm.CharmClient;
import svenhjol.charm.base.CharmClientLoader;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.helper.StringHelper;
import svenhjol.charm.handler.ColoredGlintHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@OnlyIn(Dist.CLIENT)
public class ClientHandler {
    public static Map<String, CharmClientModule> LOADED_MODULES = new TreeMap<>();

    public static IEventBus MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    public static IEventBus FORGE_EVENT_BUS = MinecraftForge.EVENT_BUS;

    public static ClientHandler INSTANCE = new ClientHandler();

    private static final List<Class<? extends CharmClientModule>> ENABLED_MODULES = new ArrayList<>(); // this is a cache of enabled classes

    private ClientHandler() {
        // register forge events
        MOD_EVENT_BUS.addListener(this::onClientSetup);
    }

    public void addLoader(CharmClientLoader loader) {
        // subscribe the loader to forge events
        MOD_EVENT_BUS.addListener(loader::onClientSetup);
        MOD_EVENT_BUS.addListener(loader::onTextureStitch);

        CharmClient.LOG.info("Subscribed client '" + loader.getModId() + "' to Forge event bus");
    }

    public void register(CharmClientModule module) {
        LOADED_MODULES.put(module.getName(), module);

        CharmClient.LOG.debug("Registering module " + module.getName());
        module.register();
    }

    public void init(CharmClientModule module) {
        // this is a cache for quick lookup of enabled classes
        ENABLED_MODULES.add(module.getClass());

        // subscribe the module to the event bus if required
        if (module.getModule().hasSubscriptions)
            FORGE_EVENT_BUS.register(module);

        CharmClient.LOG.info("Initialising module " + module.getName());
        module.init();
    }

    @Nullable
    public static CharmClientModule getModule(String moduleName) {
        return LOADED_MODULES.getOrDefault(StringHelper.snakeToUpperCamel(moduleName), null);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        ColoredGlintHandler.init(); // load late so that buffer builders are populated
    }

    /**
     * Use this within static hook methods for quick check if a module is enabled.
     * @param clazz Module to check
     * @return True if the module is enabled
     */
    public static boolean enabled(Class<? extends CharmClientModule> clazz) {
        return ENABLED_MODULES.contains(clazz);
    }
}

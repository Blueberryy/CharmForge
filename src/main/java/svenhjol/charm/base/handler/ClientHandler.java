package svenhjol.charm.base.handler;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import svenhjol.charm.CharmClient;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.StringHelper;
import svenhjol.charm.handler.ColoredGlintHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import static svenhjol.charm.base.handler.ModuleHandler.FORGE_EVENT_BUS;
import static svenhjol.charm.base.handler.ModuleHandler.MOD_EVENT_BUS;

public class ClientHandler {
    public static Map<String, CharmClientModule> LOADED_MODULES = new TreeMap<>();
    private static boolean hasInit = false;

    public static void init() {
        if (hasInit)
            return;

        // register forge events
        MOD_EVENT_BUS.addListener(ClientHandler::onClientSetup);
        MOD_EVENT_BUS.addListener(ClientHandler::onTextureStitch);

        // create all charm-based client modules
        instantiateModules();

        // early init, always run, use for registering things
        eachModule(CharmClientModule::register);

        hasInit = true;
    }

    private static void instantiateModules() {
        ModuleHandler.LOADED_MODULES.forEach((modId, module) -> {
            CharmClientModule client;

            Class<? extends CharmClientModule> clazz = module.client;
            if (clazz == null || clazz == CharmClientModule.class)
                return;

            try {
                 client = clazz.getConstructor(CharmModule.class).newInstance(module);
            } catch (Exception e) {
                CharmClient.LOG.error("Failed to create the client for " + module.getName());
                throw new RuntimeException("The chickens escaped");
            }

            String moduleName = module.getName();
            ClientHandler.LOADED_MODULES.put(moduleName, client);
        });
    }

    @Nullable
    public static CharmClientModule getModule(String moduleName) {
        return LOADED_MODULES.getOrDefault(StringHelper.snakeToUpperCamel(moduleName), null);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        // iterate all client modules, subscribe to forge bus if annotated with hasSubscriptions
        eachEnabledModule(clientModule -> {
            if (clientModule.getModule().hasSubscriptions)
                FORGE_EVENT_BUS.register(clientModule);

            clientModule.init();
        });

        ColoredGlintHandler.init(); // load late so that buffer builders are populated
    }

    public static void onTextureStitch(TextureStitchEvent event) {
        eachEnabledModule(module -> module.textureStitch(event));
    }

    private static void eachModule(Consumer<CharmClientModule> consumer) {
        LOADED_MODULES.values().forEach(consumer);
    }

    private static void eachEnabledModule(Consumer<CharmClientModule> consumer) {
        LOADED_MODULES.values()
            .stream()
            .filter(m -> m.enabled)
            .forEach(consumer);
    }
}

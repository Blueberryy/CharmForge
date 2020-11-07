package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.InventoryButtonClient;

@Module(mod = Charm.MOD_ID, alwaysEnabled = true, description = "Core configuration values.")
public class Core extends CharmModule {
    @Config(name = "Debug mode", description = "If true, routes additional debug messages into the standard game log.")
    public static boolean debug = false;

    @Config(name = "Inventory button return", description = "If inventory crafting or inventory ender chest modules are enabled, pressing escape or inventory key returns you to the inventory rather than closing the window.")
    public static boolean inventoryButtonReturn = false;

    public InventoryButtonClient inventoryButtonClient;

    @Override
    public void clientRegister() {
        inventoryButtonClient = new InventoryButtonClient();
        ModuleHandler.FORGE_EVENT_BUS.register(inventoryButtonClient);
    }
}

package svenhjol.charm.client;

import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ClientHandler;

public class CoreClient extends CharmClientModule {
    public final InventoryButtonClient inventoryButtonClient;

    public CoreClient(CharmModule module) {
        super(module);
        this.inventoryButtonClient = new InventoryButtonClient(module);
    }

    @Override
    public void register() {
        ClientHandler.FORGE_EVENT_BUS.register(inventoryButtonClient);
    }

    @Override
    public void init() {
        this.inventoryButtonClient.init();
    }
}

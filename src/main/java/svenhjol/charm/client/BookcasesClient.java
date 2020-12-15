package svenhjol.charm.client;

import net.minecraft.client.gui.ScreenManager;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.gui.SimpleCharmScreen;
import svenhjol.charm.module.Bookcases;

public class BookcasesClient extends CharmClientModule {
    public BookcasesClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Bookcases.CONTAINER, SimpleCharmScreen.createFactory(2));
    }
}
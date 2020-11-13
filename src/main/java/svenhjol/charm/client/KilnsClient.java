package svenhjol.charm.client;

import net.minecraft.client.gui.ScreenManager;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.gui.KilnScreen;
import svenhjol.charm.module.Kilns;

public class KilnsClient extends CharmClientModule {
    public KilnsClient(Kilns module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Kilns.CONTAINER, KilnScreen::new);
    }
}
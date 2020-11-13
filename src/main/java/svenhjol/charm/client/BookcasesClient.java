package svenhjol.charm.client;

import net.minecraft.client.gui.ScreenManager;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.gui.BookcaseScreen;
import svenhjol.charm.module.Bookcases;

public class BookcasesClient extends CharmClientModule {
    public BookcasesClient(Bookcases module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Bookcases.CONTAINER, BookcaseScreen::new);
    }
}
package svenhjol.charm.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.gui.WoodcutterScreen;
import svenhjol.charm.module.Woodcutters;

public class WoodCuttersClient extends CharmClientModule {
    public WoodCuttersClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Woodcutters.SCREEN_HANDLER, WoodcutterScreen::new);
    }

    @Override
    public void init() {
        RenderTypeLookup.setRenderLayer(Woodcutters.WOODCUTTER, RenderType.getCutout());
    }
}

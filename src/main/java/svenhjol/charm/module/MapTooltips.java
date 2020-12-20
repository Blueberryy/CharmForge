package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.MapTooltipsClient;

@Module(mod = Charm.MOD_ID, client = MapTooltipsClient.class, description = "Show maps in tooltips.", hasSubscriptions = true)
public class MapTooltips extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:client.module.improved_tooltips_module") || override;
    }
}

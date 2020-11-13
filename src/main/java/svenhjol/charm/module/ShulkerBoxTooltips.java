package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.ShulkerBoxTooltipsClient;

@Module(mod = Charm.MOD_ID, client = ShulkerBoxTooltipsClient.class, description = "Shows the contents of a Shulker Box on hover-over.", hasSubscriptions = true)
public class ShulkerBoxTooltips extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        depends(!ModHelper.isLoaded("quark") || override);
    }
}

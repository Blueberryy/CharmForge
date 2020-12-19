package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.GoldBarsBlock;
import svenhjol.charm.client.GoldBarsClient;

@Module(mod = Charm.MOD_ID, client = GoldBarsClient.class, description = "Gold variant of vanilla iron bars.")
public class GoldBars extends CharmModule {
    public static GoldBarsBlock GOLD_BARS;

    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        GOLD_BARS = new GoldBarsBlock(this);
    }

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:building.module.gold_bars_module") || override;
    }
}

package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.MapTooltipClient;

@Module(mod = Charm.MOD_ID, client = MapTooltipClient.class, description = "Show maps in tooltips.", hasSubscriptions = true)
public class MapTooltip extends CharmModule {
}

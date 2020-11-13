package svenhjol.charm.module;

import net.minecraft.item.DyeColor;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.CoreClient;

@Module(mod = Charm.MOD_ID, client = CoreClient.class, alwaysEnabled = true, description = "Core configuration values.", hasSubscriptions = true)
public class Core extends CharmModule {
    @Config(name = "Debug mode", description = "If true, routes additional debug messages into the standard game log.")
    public static boolean debug = false;

    @Config(name = "Inventory button return", description = "If inventory crafting or inventory ender chest modules are enabled, pressing escape or inventory key returns you to the inventory rather than closing the window.")
    public static boolean inventoryButtonReturn = false;

    @Config(name = "Enchantment glint color", description = "Set the default glint color for all enchanted items.")
    public static String glintColor = DyeColor.PURPLE.getString();
}

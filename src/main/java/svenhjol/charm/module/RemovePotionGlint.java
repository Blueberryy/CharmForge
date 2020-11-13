package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Removes the potion enchantment glint so you can see what the potion color is.")
public class RemovePotionGlint extends CharmModule {
    public static boolean shouldRemoveGlint() {
        return ModuleHandler.enabled(RemovePotionGlint.class);
    }
}

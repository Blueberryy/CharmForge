package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.base.integration.DummyQuarkCompat;
import svenhjol.charm.base.integration.IQuarkCompat;
import svenhjol.charm.base.integration.QuarkCompat;

@Module(mod = Charm.MOD_ID, alwaysEnabled = true, description = "Quark integration")
public class Quark extends CharmModule {
    public static IQuarkCompat compat;

    @Override
    public void register() {
        boolean hasQuark = false;

        try {
            if (ModHelper.isLoaded("quark")) {
                // if quark is not included via compileOnly in build.gradle, this should be commented out
                compat = QuarkCompat.class.getDeclaredConstructor().newInstance();
                Charm.LOG.info("Loaded Quark compatibility class");
                hasQuark = true;
            } else {
                useDummy();
            }
        } catch (Exception e) {
            Charm.LOG.error("Failed to load Quark compatibility class: " + e.getMessage());
            useDummy();
        }

        if (!hasQuark)
            return;

        // player state location callback to check if player is inside a big dungeon
        PlayerState.listeners.add((player, tag)
            -> tag.putBoolean("ruin", compat.isInBigDungeon(player)));
    }

    private void useDummy() {
        compat = new DummyQuarkCompat();
    }
}

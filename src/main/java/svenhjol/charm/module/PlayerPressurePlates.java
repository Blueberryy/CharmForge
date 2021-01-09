package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.PlayerPressurePlateBlock;

@Module(mod = Charm.MOD_ID, description = "Player-only pressure plates.")
public class PlayerPressurePlates extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    public static PlayerPressurePlateBlock PLAYER_PRESSURE_PLATE_BLOCK;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:automation.module.obsidian_plate_module") || override;
    }

    @Override
    public void register() {
        PLAYER_PRESSURE_PLATE_BLOCK = new PlayerPressurePlateBlock(this);
    }
}

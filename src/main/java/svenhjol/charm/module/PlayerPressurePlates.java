package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.PlayerPressurePlateBlock;

@Module(mod = Charm.MOD_ID, description = "Player-only pressure plates.")
public class PlayerPressurePlates extends CharmModule {
    public static PlayerPressurePlateBlock PLAYER_PRESSURE_PLATE_BLOCK;
    @Override
    public void register() {
        PLAYER_PRESSURE_PLATE_BLOCK = new PlayerPressurePlateBlock(this);
    }
}

package svenhjol.charm.base.integration;

import net.minecraft.entity.player.PlayerEntity;

public class DummyQuarkCompat implements IQuarkCompat {
    @Override
    public boolean isModuleEnabled(String moduleName) {
        return false;
    }

    @Override
    public boolean isInBigDungeon(PlayerEntity player) {
        return false;
    }
}

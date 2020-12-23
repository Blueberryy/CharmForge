package svenhjol.charm.base.integration;

import net.minecraft.entity.player.PlayerEntity;

public interface IQuarkCompat {
    boolean isModuleEnabled(String moduleName);

    boolean isInBigDungeon(PlayerEntity player);

    void forceQuarkConfigLoad();
}

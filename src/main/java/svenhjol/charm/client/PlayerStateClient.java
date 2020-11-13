package svenhjol.charm.client;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.module.PlayerState;

public class PlayerStateClient extends CharmClientModule {
    public boolean mineshaft = false;
    public boolean stronghold = false;
    public boolean fortress = false;
    public boolean shipwreck = false;
    public boolean village = false;
    public boolean isDaytime = true;

    public static PlayerStateClient INSTANCE;

    public PlayerStateClient(PlayerState module) {
        super(module);
        INSTANCE = this;
    }

    /**
     * Unpack the received server data from the NBT tag.
     */
    @OnlyIn(Dist.CLIENT)
    public void clientCallback(CompoundNBT data) {
        this.mineshaft = data.getBoolean("mineshaft");
        this.stronghold = data.getBoolean("stronghold");
        this.fortress = data.getBoolean("fortress");
        this.shipwreck = data.getBoolean("shipwreck");
        this.village = data.getBoolean("village");
        this.isDaytime = data.getBoolean("day");
    }
}

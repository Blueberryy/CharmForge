package svenhjol.charm.base;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class CharmModule {
    public boolean enabled = true;
    public boolean enabledByDefault = true;
    public boolean alwaysEnabled = false;
    public boolean hasSubscriptions = false;
    public String description = "";
    public String mod = "";
    public Class<? extends CharmClientModule> client = null;

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public List<ResourceLocation> getRecipesToRemove() {
        return new ArrayList<>();
    }

    public boolean depends() {
        return true; // this.enabled checks conditions here after forge syncs its config (after register, before init)
    }

    public void register() {
        // run on both sides, even if module disabled (this.enabled is available)
    }

    public void init() {
        // run on both sides, only if module enabled
    }

    public void loadWorld(MinecraftServer server) {
        // run on server on world load, only executed if module enabled
    }
}

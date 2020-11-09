package svenhjol.charm.base;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class CharmModule {
    public boolean enabled = true;
    public boolean enabledByDefault = true;
    public boolean alwaysEnabled = false;
    public boolean hasSubscriptions = false;
    public String description = "";
    public String mod = "";
    public String category = "";

    public void depends(boolean test) {
        this.enabled = this.enabled && test;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public List<ResourceLocation> getRecipesToRemove() {
        return new ArrayList<>();
    }

    public void register() {
        // run on both sides, even if not enabled (enabled flag is available)
    }

    public void clientRegister() {
        // run on client, even if not enabled (enabled flag is available)
    }

    public void clientTextureStitch(TextureStitchEvent event) {
        // runs on client when Forge's texture stich event is fired
    }

    public void init() {
        // run on both sides, only executed if module enabled
    }

    public void clientInit() {
        // run on client, only executed if module enabled
    }

    public void loadWorld(MinecraftServer server) {
        // run on server on world load, only executed if module enabled
    }

    public void clientJoinWorld(Minecraft client) {
        // run on client on world load, only executed if module enabled
    }

    public void serverInit(MinecraftServer server) {
        // run only on the dedicated server world load, only executed if module enabled
    }
}

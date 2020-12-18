package svenhjol.charm.base;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent;

public abstract class CharmClientModule {
    protected CharmModule module;
    public boolean hasSubscriptions;

    public CharmClientModule(CharmModule module) {
        this.module = module;
    }

    public CharmModule getModule() {
        return module;
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void register() {
        // run on client side, even if module disabled
    }

    public void textureStitch(TextureStitchEvent event) {
        // runs on client when Forge's texture stich event is fired
    }

    public void init() {
        // run on client side, only if module enabled
    }

    public void loadWorld(Minecraft client) {
        // run on client side, only if module enabled
    }
}

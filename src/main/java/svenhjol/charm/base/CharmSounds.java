package svenhjol.charm.base;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.RegistryHandler;

import java.util.HashMap;
import java.util.Map;

public class CharmSounds {
    public static Map<ResourceLocation, SoundEvent> REGISTER = new HashMap<>();

    public static final SoundEvent BOOKSHELF_OPEN = createSound("bookshelf_open");
    public static final SoundEvent BOOKSHELF_CLOSE = createSound("bookshelf_close");

    public static void init() {
        REGISTER.forEach(RegistryHandler::sound);
    }

    public static SoundEvent createSound(String name) {
        ResourceLocation id = new ResourceLocation(Charm.MOD_ID, name);
        SoundEvent sound = new SoundEvent(id);
        REGISTER.put(id, sound);
        return sound;
    }
}

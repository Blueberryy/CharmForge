package svenhjol.charm.mixin.accessor;

import net.minecraft.client.audio.SoundEngine;
import net.minecraft.client.audio.SoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundHandler.class)
public interface SoundHandlerAccessor {
    @Accessor()
    SoundEngine getSndManager();
}

package svenhjol.charm.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEngine;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SoundEngine.class)
public interface SoundEngineAccessor {
    @Accessor
    Multimap<SoundCategory, ISound> getCategorySounds();
}

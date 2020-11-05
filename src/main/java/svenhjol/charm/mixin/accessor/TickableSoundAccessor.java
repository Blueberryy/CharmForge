package svenhjol.charm.mixin.accessor;

import net.minecraft.client.audio.TickableSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TickableSound.class)
public interface TickableSoundAccessor {
    @Accessor
    void setDonePlaying(boolean done);

    @Accessor
    boolean getDonePlaying();
}

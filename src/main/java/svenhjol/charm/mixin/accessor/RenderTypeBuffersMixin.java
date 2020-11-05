package svenhjol.charm.mixin.accessor;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.SortedMap;

@Mixin(RenderTypeBuffers.class)
public interface RenderTypeBuffersMixin {
    @Accessor
    SortedMap<RenderType, BufferBuilder> getFixedBuffers();
}

package svenhjol.charm.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import svenhjol.charm.module.SnowStorms;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(
        method = "tickEnvironment",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/profiler/IProfiler;endStartSection(Ljava/lang/String;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void hookTryPlaceSnow(Chunk chunk, int randomTickSpeed, CallbackInfo ci, ChunkPos chunkPos, boolean isRaining, int chunkX, int chunkZ) {
        SnowStorms.tryPlaceSnow((ServerWorld)(Object)this, chunkX, chunkZ);
    }
}

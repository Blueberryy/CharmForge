package svenhjol.charm.mixin.accessor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.PatrolSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Random;

@Mixin(PatrolSpawner.class)
public interface PatrolSpawnerAccessor {
    @Accessor("field_222698_b")
    void setTicksUntilNextSpawn(int ticks);

    @Invoker
    boolean invokeSpawnPatroller(ServerWorld world, BlockPos pos, Random random, boolean captain);
}

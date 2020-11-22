package svenhjol.charm.mixin.accessor;

import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
    @Accessor("field_241104_N_")
    List<ISpecialSpawner> getSpawners();

    @Invoker
    void invokeWakeUpAllPlayers();

    @Invoker
    void invokeResetRainAndThunder();
}

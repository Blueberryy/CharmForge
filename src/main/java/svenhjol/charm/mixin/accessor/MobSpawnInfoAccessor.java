package svenhjol.charm.mixin.accessor;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(MobSpawnInfo.class)
public interface MobSpawnInfoAccessor {
    @Accessor
    Map<EntityClassification, List<MobSpawnInfo.Spawners>> getSpawners();

    @Accessor
    Map<EntityType<?>, MobSpawnInfo.SpawnCosts> getSpawnCosts();

    @Accessor
    void setSpawners(Map<EntityClassification, List<MobSpawnInfo.Spawners>> spawners);

    @Accessor
    void setSpawnCosts(Map<EntityType<?>, MobSpawnInfo.SpawnCosts> spawnCosts);
}

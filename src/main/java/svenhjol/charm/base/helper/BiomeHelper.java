package svenhjol.charm.base.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.mixin.accessor.BiomeGenerationSettingsAccessor;
import svenhjol.charm.mixin.accessor.MobSpawnInfoAccessor;

import java.util.*;
import java.util.function.Supplier;

public class BiomeHelper {
    public static List<String> BADLANDS = new ArrayList<>();
    public static List<String> DESERT = new ArrayList<>();
    public static List<String> END = new ArrayList<>();
    public static List<String> FOREST = new ArrayList<>();
    public static List<String> JUNGLE = new ArrayList<>();
    public static List<String> MOUNTAINS = new ArrayList<>();
    public static List<String> NETHER = new ArrayList<>();
    public static List<String> PLAINS = new ArrayList<>();
    public static List<String> SAVANNA = new ArrayList<>();
    public static List<String> SNOWY = new ArrayList<>();
    public static List<String> TAIGA = new ArrayList<>();

    public static Biome getBiome(ServerWorld world, BlockPos pos) {
        BiomeManager biomeAccess = world.getBiomeManager();
        return biomeAccess.getBiome(pos);
    }

    public static Biome getBiomeFromBiomeKey(RegistryKey<Biome> biomeKey) {
        return WorldGenRegistries.BIOME.getValueForKey(biomeKey);
    }

    public static Optional<RegistryKey<Biome>> getBiomeKeyAtPosition(ServerWorld world, BlockPos pos) {
        return world.func_242406_i(pos);
    }

    public static BlockPos locateBiome(RegistryKey<Biome> biomeKey, ServerWorld world, BlockPos pos) {
        Biome biome = world.getRegistryManager().get(Registry.BIOME_KEY).get(biomeKey);
        return locateBiome(biome, world, pos);
    }

    public static BlockPos locateBiome(Biome biome, ServerWorld world, BlockPos pos) {
        return world.func_241116_a_(biome, pos, 6400, 8);
    }

    public static void addStructureFeature(Biome biome, StructureFeature<?, ?> structureFeature) {
        GenerationSettings settings = biome.getGenerationSettings();
        checkGenerationSettingsMutable(settings);
        ((BiomeGenerationSettingsAccessor)settings).getStructures().add(() -> structureFeature);
    }

    public static void addSpawnEntry(Biome biome, SpawnGroup group, EntityType<?> entity, int weight, int minGroupSize, int maxGroupSize) {
        SpawnSettings settings = biome.getSpawnSettings();
        checkSpawnSettingsMutable(settings);

        // TODO: revise all this
        Map<SpawnGroup, List<SpawnEntry>> spawners = ((MobSpawnInfoAccessor) settings).getSpawners();
        spawners.get(group).add(new SpawnEntry(entity, weight, minGroupSize, maxGroupSize));
        ((MobSpawnInfoAccessor)settings).setSpawners(spawners);
    }

    /**
     * Evil hack until there's a better way to add structures to biomes
     */
    private static void checkGenerationSettingsMutable(GenerationSettings settings) {
        List<Supplier<StructureFeature<?, ?>>> existing = ((BiomeGenerationSettingsAccessor)settings).getStructures();
        if (existing instanceof ImmutableList)
            ((BiomeGenerationSettingsAccessor)settings).setStructures(new ArrayList<>(existing));
    }

    /**
     * Evil hack until there's a better way to add mobs to biomes
     */
    private static void checkSpawnSettingsMutable(SpawnSettings settings) {
        Map<SpawnGroup, List<SpawnEntry>> spawners = ((MobSpawnInfoAccessor) settings).getSpawners();
        Map<EntityType<?>, SpawnSettings.SpawnDensity> spawnCosts = ((MobSpawnInfoAccessor) settings).getSpawnCosts();

        if (spawners instanceof ImmutableMap) {
            // have to make each list mutable as well. BIOME API OMFG.
            HashMap<SpawnGroup, List<SpawnEntry>> mutable = new HashMap<>(spawners);

            spawners.forEach((spawnGroup, spawnEntries) ->
                mutable.put(spawnGroup, new ArrayList<>(spawnEntries)));

            ((MobSpawnInfoAccessor)settings).setSpawners(new HashMap<>(mutable));
        }

        // may need costs in future, for now it's unused
        if (spawnCosts instanceof ImmutableMap) {
            ((MobSpawnInfoAccessor)settings).setSpawnCosts(new HashMap<>(spawnCosts));
        }
    }
}

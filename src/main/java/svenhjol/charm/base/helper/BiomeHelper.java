package svenhjol.charm.base.helper;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BiomeHelper {
    public static List<RegistryKey<Biome>> BADLANDS = new ArrayList<>();
    public static List<RegistryKey<Biome>> DESERT = new ArrayList<>();
    public static List<RegistryKey<Biome>> END = new ArrayList<>();
    public static List<RegistryKey<Biome>> FOREST = new ArrayList<>();
    public static List<RegistryKey<Biome>> JUNGLE = new ArrayList<>();
    public static List<RegistryKey<Biome>> MOUNTAINS = new ArrayList<>();
    public static List<RegistryKey<Biome>> NETHER = new ArrayList<>();
    public static List<RegistryKey<Biome>> PLAINS = new ArrayList<>();
    public static List<RegistryKey<Biome>> SAVANNA = new ArrayList<>();
    public static List<RegistryKey<Biome>> SNOWY = new ArrayList<>();
    public static List<RegistryKey<Biome>> TAIGA = new ArrayList<>();

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
        Biome biome = world.func_241828_r().getRegistry(Registry.BIOME_KEY).getValueForKey(biomeKey);
        return locateBiome(biome, world, pos);
    }

    public static BlockPos locateBiome(Biome biome, ServerWorld world, BlockPos pos) {
        return world.func_241116_a_(biome, pos, 6400, 8);
    }
}

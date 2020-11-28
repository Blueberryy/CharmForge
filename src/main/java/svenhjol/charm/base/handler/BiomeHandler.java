package svenhjol.charm.base.handler;

import net.minecraft.world.biome.Biomes;
import svenhjol.charm.base.helper.BiomeHelper;

import java.util.Arrays;

public class BiomeHandler {
    public static void init() {
        BiomeHelper.BADLANDS.addAll(Arrays.asList(Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU));
        BiomeHelper.DESERT.addAll(Arrays.asList(Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.DESERT_LAKES));
        BiomeHelper.END.addAll(Arrays.asList(Biomes.END_BARRENS, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS));
        BiomeHelper.FOREST.addAll(Arrays.asList(Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.DARK_FOREST, Biomes.DARK_FOREST_HILLS));
        BiomeHelper.JUNGLE.addAll(Arrays.asList(Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.MODIFIED_JUNGLE));
        BiomeHelper.MOUNTAINS.addAll(Arrays.asList(Biomes.MOUNTAINS, Biomes.GRAVELLY_MOUNTAINS, Biomes.WOODED_MOUNTAINS, Biomes.SNOWY_MOUNTAINS));
        BiomeHelper.NETHER.addAll(Arrays.asList(Biomes.SOUL_SAND_VALLEY, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES));
        BiomeHelper.PLAINS.addAll(Arrays.asList(Biomes.PLAINS, Biomes.SWAMP, Biomes.SUNFLOWER_PLAINS, Biomes.FLOWER_FOREST));
        BiomeHelper.SAVANNA.addAll(Arrays.asList(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.SHATTERED_SAVANNA));
        BiomeHelper.SNOWY.addAll(Arrays.asList(Biomes.SNOWY_TUNDRA, Biomes.SNOWY_TAIGA, Biomes.ICE_SPIKES));
        BiomeHelper.TAIGA.addAll(Arrays.asList(Biomes.TAIGA, Biomes.TAIGA_HILLS, Biomes.TAIGA_MOUNTAINS));
    }
}

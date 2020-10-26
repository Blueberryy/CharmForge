package svenhjol.charm.base.helper;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.template.StructureProcessor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StructureHelper {
    public static List<StructureProcessor> SINGLE_POOL_ELEMENT_PROCESSORS = new ArrayList<>();

    public static void addToBiome(List<String> biomeGroup, StructureFeature<?, ?> configuredFeature) {
        biomeGroup.forEach(id -> WorldGenRegistries.BIOME.getOptional(new ResourceLocation(id))
            .ifPresent(biome -> BiomeHelper.addStructureFeature(biome, configuredFeature)));
    }
}

package svenhjol.charm.mixin.accessor;

import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(BiomeGenerationSettings.class)
public interface BiomeGenerationSettingsAccessor {
    @Accessor
    List<Supplier<StructureFeature<?, ?>>> getStructures();

    @Accessor
    void setStructures(List<Supplier<StructureFeature<?, ?>>> structures);
}

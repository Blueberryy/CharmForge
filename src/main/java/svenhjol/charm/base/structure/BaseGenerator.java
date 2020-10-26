package svenhjol.charm.base.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class BaseGenerator {
    @Nullable
    protected static StructurePool registerPool(ResourceLocation startPool, List<BaseStructure> structures) {
        if (structures.isEmpty())
            return emptyPool(startPool);

        // this is populated with starts for each custom ruin
        List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> starts = new ArrayList<>();

        // iterate over each custom structure, get all the start pools, and put them into the starts list
        structures.forEach(structure -> starts.addAll(structure.getStarts()));

        // return the start pool containing all the custom structure starts
        return StructurePools.register(
            new StructurePool(
                startPool,
                new ResourceLocation("empty"),
                ImmutableList.copyOf(starts),
                StructurePool.Projection.RIGID
            )
        );
    }

    protected static StructurePool emptyPool(ResourceLocation poolName) {
        return new StructurePool(
            poolName,
            new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(StructurePoolElement.method_30438(), 1)),
            StructurePool.Projection.RIGID
        );
    }
}

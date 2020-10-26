package svenhjol.charm.base.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseStructure {
    private final String modId;
    private final String mainFolder;
    private final String structureName;
    private final List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> starts = new ArrayList<>();

    public BaseStructure(String modId, String mainFolder, String structureName) {
        this.modId = modId;
        this.mainFolder = mainFolder;
        this.structureName = structureName;
    }

    public List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> getStarts() {
        return starts;
    }

    protected void addStart(String pieceName, int weight) {
        starts.add(Pair.of(StructurePoolElement.method_30435(getPiecePath(pieceName), StructureProcessorLists.EMPTY), weight));
    }

    protected void registerPool(String poolName, Map<String, Integer> elements) {
        final List<Pair<Function<StructurePool.Projection, ? extends StructurePoolElement>, Integer>> pieces = new ArrayList<>();

        elements.forEach((piece, weight) ->
            pieces.add(Pair.of(StructurePoolElement.method_30435(getPiecePath(piece), StructureProcessorLists.EMPTY), weight)));

        StructurePools.register(new StructurePool(
            getPoolPath(poolName),
            getPoolPath("ends"),
            ImmutableList.copyOf(pieces),
            StructurePool.Projection.RIGID
        ));
    }

    protected String getPiecePath(String piece) {
        return modId + ":" + mainFolder + "/" + structureName + "/" + piece;
    }

    protected ResourceLocation getPoolPath(String pool) {
        return new ResourceLocation(modId, mainFolder + "/" + structureName + "/" + pool);
    }
}

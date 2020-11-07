package svenhjol.charm.base.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseStructure {
    private final String modId;
    private final String mainFolder;
    private final String structureName;
    private final List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> starts = new ArrayList<>();

    public BaseStructure(String modId, String mainFolder, String structureName) {
        this.modId = modId;
        this.mainFolder = mainFolder;
        this.structureName = structureName;
    }

    public List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> getStarts() {
        return starts;
    }

    protected void addStart(String pieceName, int weight) {
        starts.add(Pair.of(JigsawPiece.func_242861_b(getPiecePath(pieceName), ProcessorLists.field_244101_a), weight));
    }

    protected void registerPool(String poolName, Map<String, Integer> elements) {
        final List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> pieces = new ArrayList<>();

        elements.forEach((piece, weight) ->
            pieces.add(Pair.of(JigsawPiece.func_242861_b(getPiecePath(piece), ProcessorLists.field_244101_a), weight)));

        JigsawPatternRegistry.func_244094_a(new JigsawPattern(
            getPoolPath(poolName),
            getPoolPath("ends"),
            ImmutableList.copyOf(pieces),
            JigsawPattern.PlacementBehaviour.RIGID
        ));
    }

    protected String getPiecePath(String piece) {
        return modId + ":" + mainFolder + "/" + structureName + "/" + piece;
    }

    protected ResourceLocation getPoolPath(String pool) {
        return new ResourceLocation(modId, mainFolder + "/" + structureName + "/" + pool);
    }
}

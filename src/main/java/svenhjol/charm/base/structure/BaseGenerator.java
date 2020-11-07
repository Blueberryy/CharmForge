package svenhjol.charm.base.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class BaseGenerator {
    @Nullable
    protected static JigsawPattern registerPool(ResourceLocation startPool, List<BaseStructure> structures) {
        if (structures.isEmpty())
            return emptyPool(startPool);

        // this is populated with starts for each custom ruin
        List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> starts = new ArrayList<>();

        // iterate over each custom structure, get all the start pools, and put them into the starts list
        structures.forEach(structure -> starts.addAll(structure.getStarts()));

        // return the start pool containing all the custom structure starts
        return JigsawPatternRegistry.func_244094_a(
            new JigsawPattern(
                startPool,
                new ResourceLocation("empty"),
                ImmutableList.copyOf(starts),
                JigsawPattern.PlacementBehaviour.RIGID
            )
        );
    }

    protected static JigsawPattern emptyPool(ResourceLocation poolName) {
        return new JigsawPattern(
            poolName,
            new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(JigsawPiece.func_242864_g(), 1)),
            JigsawPattern.PlacementBehaviour.RIGID
        );
    }
}

package svenhjol.charm.base.helper;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.LegacySingleJigsawPiece;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;
import svenhjol.charm.base.enums.ICharmEnum;
import svenhjol.charm.mixin.accessor.JigsawPatternAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
public class StructureHelper {
    public static Map<ResourceLocation, JigsawPattern> vanillaPools = new HashMap<>();

    public static JigsawPattern getVanillaPool(ResourceLocation id) {
        if (!vanillaPools.containsKey(id)) {
            JigsawPattern pool = WorldGenRegistries.JIGSAW_POOL.getOrDefault(id);

            // convert elementCounts to mutable list
            List<Pair<JigsawPiece, Integer>> elementCounts = ((JigsawPatternAccessor) pool).getRawTemplates();
            ((JigsawPatternAccessor)pool).setRawTemplates(new ArrayList<>(elementCounts));

            if (false) { // DELETES ALL IN POOL, DO NOT USE!
                ((JigsawPatternAccessor) pool).setRawTemplates(new ArrayList<>());
            }

            vanillaPools.put(id, pool);
        }

        return vanillaPools.get(id);
    }

    public static void addStructurePoolElement(ResourceLocation poolId, ResourceLocation pieceId, StructureProcessorList processor, JigsawPattern.PlacementBehaviour projection, int count) {
        Pair<Function<JigsawPattern.PlacementBehaviour, LegacySingleJigsawPiece>, Integer> pair =
            Pair.of(JigsawPiece.func_242851_a(pieceId.toString(), processor), count);

        JigsawPiece element = pair.getFirst().apply(projection);
        JigsawPattern pool = getVanillaPool(poolId);

        // add custom piece to the element counts
        ((JigsawPatternAccessor)pool).getRawTemplates().add(Pair.of(element, count));

        // add custom piece to the elements
        for (int i = 0; i < count; i++) {
            ((JigsawPatternAccessor)pool).getJigsawPieces().add(element);
        }
    }

    public static void addVillageHouse(VillageType type, ResourceLocation pieceId, int count) {
        ResourceLocation houses = new ResourceLocation("village/" + type.getString() + "/houses");
        StructureProcessorList processor = ProcessorLists.field_244107_g; // MOSSIFY 10%
        JigsawPattern.PlacementBehaviour projection = JigsawPattern.PlacementBehaviour.RIGID;
        addStructurePoolElement(houses, pieceId, processor, projection, count);
    }

    public enum VillageType implements ICharmEnum {
        DESERT,
        PLAINS,
        SAVANNA,
        SNOWY,
        TAIGA
    }
}

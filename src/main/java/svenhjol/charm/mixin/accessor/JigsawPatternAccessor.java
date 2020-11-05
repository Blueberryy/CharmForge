package svenhjol.charm.mixin.accessor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(JigsawPattern.class)
public interface JigsawPatternAccessor {
    @Accessor
    List<JigsawPiece> getJigsawPieces();

    @Accessor
    List<Pair<JigsawPiece, Integer>> getRawTemplates();

    @Accessor
    void setRawTemplates(List<Pair<JigsawPiece, Integer>> list);
}

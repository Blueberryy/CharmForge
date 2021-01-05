package svenhjol.charm.mixin.accessor;

import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = {"net/minecraft/world/gen/feature/structure/MineshaftPieces$Piece"})
public interface MineshaftPiecesAccessor {
    @Accessor
    MineshaftStructure.Type getMineShaftType();
}

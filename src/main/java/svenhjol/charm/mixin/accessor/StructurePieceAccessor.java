package svenhjol.charm.mixin.accessor;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructurePiece.class)
public interface StructurePieceAccessor {
    @Accessor()
    MutableBoundingBox getBoundingBox();

    @Invoker
    void invokeSetBlockState(ISeedReader worldIn, BlockState blockstateIn, int x, int y, int z, MutableBoundingBox boundingboxIn);

    @Invoker
    int invokeGetXWithOffset(int x, int z);

    @Invoker
    int invokeGetYWithOffset(int y);

    @Invoker
    int invokeGetZWithOffset(int x, int z);
}

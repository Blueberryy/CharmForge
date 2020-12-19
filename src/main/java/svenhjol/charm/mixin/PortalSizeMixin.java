package svenhjol.charm.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.PortalSize;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import svenhjol.charm.module.MorePortalFrames;

@Mixin(PortalSize.class)
public class PortalSizeMixin {
    @Final
    @Shadow private static final AbstractBlock.IPositionPredicate POSITION_PREDICATE = (blockState, blockView, blockPos) -> MorePortalFrames.isValidBlock(blockState);
}

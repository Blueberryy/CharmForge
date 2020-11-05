package svenhjol.charm.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlock;

public class SmoothGlowstoneBlock extends CharmBlock {
    public SmoothGlowstoneBlock(CharmModule module) {
        super(module, "smooth_glowstone", Properties.from(Blocks.GLOWSTONE));
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(world, pos, state, player);

    }
}

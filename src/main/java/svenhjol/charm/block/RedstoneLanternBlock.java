package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.base.CharmModule;

import java.util.Random;

public class RedstoneLanternBlock extends BaseLanternBlock {
    public static BooleanProperty LIT = BlockStateProperties.LIT;

    public RedstoneLanternBlock(CharmModule module) {
        super(module, "redstone_lantern", AbstractBlock.Properties.from(Blocks.LANTERN)
            .setLightLevel(p -> p.get(BlockStateProperties.LIT) ? 15 : 0));

        this.setDefaultState(this.getDefaultState().with(LIT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (state != null)
            return state.with(LIT, ctx.getWorld().isBlockPowered(ctx.getPos()));

        return null;
    }

    @Override
    public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.tick(state, world, pos, random);
        if (state.get(LIT) && !world.isBlockPowered(pos))
            world.setBlockState(pos, state.func_235896_a_(LIT), 2);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isRemote) {
            boolean flag = state.get(LIT);
            if (flag != world.isBlockPowered(pos)) {
                if (flag) {
                    world.getPendingBlockTicks().scheduleTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.func_235896_a_(LIT), 2);
                }
            }
        }
    }
}

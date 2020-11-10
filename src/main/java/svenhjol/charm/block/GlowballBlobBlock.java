package svenhjol.charm.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlock;

import java.util.HashMap;
import java.util.Map;

public class GlowballBlobBlock extends CharmBlock implements IWaterLoggable {
    public static final Map<Direction, VoxelShape> SHAPE = new HashMap<>();
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public GlowballBlobBlock(CharmModule module) {
        super(module, "glowball_blob", Properties.from(Blocks.REDSTONE_WIRE)
            .setLightLevel(l -> 8));

        this.setDefaultState(getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.get(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = this.getDefaultState();
        World world = context.getWorld();
        BlockPos pos = context.getPos();

        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        boolean isWaterlogged = fluidstate.getFluid() == Fluids.WATER;

        Direction[] directions = context.getNearestLookingDirections();
        for (Direction direction : directions) {
            Direction opposite = direction.getOpposite();
            state = state.with(FACING, opposite);

            if (state.isValidPosition(world, pos)) {
                return state.with(WATERLOGGED, isWaterlogged);
            }
        }

        return null;
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
        if (state.get(WATERLOGGED))
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));

        return direction.getOpposite() == state.get(FACING) && !state.isValidPosition(world, pos) ? Blocks.AIR.getDefaultState() : state;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (!state.get(BlockStateProperties.WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, true), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        BlockState blockState = world.getBlockState(blockPos);
        return blockState.isSolidSide(world, blockPos, direction);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockItem createBlockItem(ResourceLocation id) {
        return null;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        // don't
    }

    static {
        SHAPE.put(Direction.UP, Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D));
        SHAPE.put(Direction.DOWN, Block.makeCuboidShape(3.0D, 15.0D, 3.0D, 13.0D, 16.0D, 13.0D));
        SHAPE.put(Direction.EAST, Block.makeCuboidShape(0.0D, 3.0D, 3.0D, 1.0D, 13.0D, 13.0D));
        SHAPE.put(Direction.SOUTH, Block.makeCuboidShape(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 1.0D));
        SHAPE.put(Direction.WEST, Block.makeCuboidShape(15.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D));
        SHAPE.put(Direction.NORTH, Block.makeCuboidShape(3.0D, 3.0D, 15.0D, 13.0D, 13.0D, 16.0D));
    }
}

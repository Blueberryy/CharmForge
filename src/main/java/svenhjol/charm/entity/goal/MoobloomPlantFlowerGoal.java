package svenhjol.charm.entity.goal;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import svenhjol.charm.entity.MoobloomEntity;

public class MoobloomPlantFlowerGoal extends Goal {
    private final MoobloomEntity mob;
    private final World world;
    private boolean planting;

    public MoobloomPlantFlowerGoal(MoobloomEntity mob) {
        this.mob = mob;
        this.world = mob.world;
    }

    @Override
    public boolean shouldExecute() {
        if (!world.getGameRules().getBoolean(GameRules.MOB_GRIEFING))
            return false;

        if (planting)
            return false;

        if (mob.isChild())
            return false;

        if (mob.getRNG().nextInt(1000) != 0)
            return false;

        BlockPos pos = mob.getPosition();
        return world.getBlockState(pos).isAir() && world.getBlockState(pos.down()).isIn(Blocks.GRASS_BLOCK);
    }

    @Override
    public void startExecuting() {
        this.planting = true;
    }

    @Override
    public void resetTask() {
        this.planting = false;
    }

    @Override
    public void tick() {
        if (planting) {
            BlockPos pos = mob.getPosition();
            if (world.getBlockState(pos).isAir() && world.getBlockState(pos.down()).isIn(Blocks.GRASS_BLOCK)) {
                world.playEvent(2001, pos, Block.getStateId(Blocks.GRASS_BLOCK.getDefaultState()));
                world.setBlockState(pos, mob.getMoobloomType().getFlower(), 2);
            }
            planting = false;
        }
    }
}

package svenhjol.charm.entity.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.module.BlockOfEnderPearls;

import java.util.EnumSet;
import java.util.Random;

public class FormEndermiteGoal extends RandomWalkingGoal {
    private final SilverfishEntity silverfish;
    private Direction facing;
    private boolean merge;

    public FormEndermiteGoal(SilverfishEntity silverfish) {
        super(silverfish, 0.6D);
        this.silverfish = silverfish;
        setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!silverfish.getEntityWorld().getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            return false;
        } else if (silverfish.getAttackTarget() != null) {
            return false;
        } else if (!silverfish.getNavigator().noPath()) {
            return false;
        } else {
            Random random = silverfish.getRNG();

            if (random.nextFloat() < 0.8D) {
                facing = Direction.getRandomDirection(random);
                BlockPos pos = getSilverfishPosition(silverfish).offset(facing);
                BlockState state = silverfish.getEntityWorld().getBlockState(pos);

                if (state.getBlock() == BlockOfEnderPearls.ENDER_PEARL_BLOCK) {
                    merge = true;
                    return true;
                }
            }

            merge = false;
            return super.shouldExecute();
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !merge && super.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        World world = silverfish.getEntityWorld();
        if (world.isRemote)
            return;

        BlockPos silverfishPos = getSilverfishPosition(silverfish);
        if (facing == null)
            return;

        BlockPos pos = silverfishPos.offset(facing);
        BlockState state = world.getBlockState(pos);
        BlockPos entityPos = new BlockPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        if (state.getBlock() == BlockOfEnderPearls.ENDER_PEARL_BLOCK) {
            EndermiteEntity endermite = MobHelper.spawn(EntityType.ENDERMITE, (ServerWorld)world, entityPos, SpawnReason.CONVERSION);

            if (endermite != null) {
                world.removeBlock(pos, false);
                silverfish.spawnExplosionParticle();
                silverfish.remove();
                world.addEntity(endermite);
            }
        }
    }

    private BlockPos getSilverfishPosition(SilverfishEntity silverfishEntity) {
        BlockPos entityPos = silverfishEntity.getPosition();
        return new BlockPos(entityPos.getX(), entityPos.getY() + 0.5D, entityPos.getZ());
    }
}

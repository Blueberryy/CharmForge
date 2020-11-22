package svenhjol.charm.entity.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import svenhjol.charm.entity.MoobloomEntity;
import svenhjol.charm.mixin.accessor.BeeEntityAccessor;
import svenhjol.charm.module.Core;

import java.util.List;
import java.util.function.Predicate;

public class BeeMoveToMoobloomGoal extends Goal {
    private static final int MAX_MOVE_TICKS = 1200;
    private static final int RANGE = 24;

    private final BeeEntity bee;
    private final World world;
    private MoobloomEntity moobloom = null;
    private int moveTicks;
    private int lastTried = 0;

    public BeeMoveToMoobloomGoal(BeeEntity bee) {
        this.bee = bee;
        this.world = bee.world;
    }

    @Override
    public boolean shouldExecute() {
        if (bee.hasNectar()) {
            if (--lastTried <= 0)
                return true;
        }

        return false;
    }

    @Override
    public void startExecuting() {
        moobloom = null;
        moveTicks = 0;
        bee.resetTicksWithoutNectar();

        AxisAlignedBB box = bee.getBoundingBox().expand(RANGE, RANGE / 2.0, RANGE);
        Predicate<MoobloomEntity> selector = entity -> !entity.isPollinated() && entity.isAlive();
        List<MoobloomEntity> entities = world.getEntitiesWithinAABB(MoobloomEntity.class, box, selector);

        if (entities.size() > 0) {
            moobloom = entities.get(world.rand.nextInt(entities.size()));
            bee.setStayOutOfHiveCountdown(MAX_MOVE_TICKS);
        } else {
            lastTried = 200;
        }

        super.startExecuting();
    }

    @Override
    public void resetTask() {
        moveTicks = 0;
        moobloom = null;
        bee.getNavigator().clearPath();
        bee.getNavigator().resetRangeMultiplier();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return moobloom != null && moobloom.isAlive() && moveTicks < MAX_MOVE_TICKS;
    }

    @Override
    public void tick() {
        moveTicks++;

        if (moobloom == null || !moobloom.isAlive())
            return;

        if (moveTicks > MAX_MOVE_TICKS) {
            moobloom = null;
        } else if (!bee.getNavigator().hasPath()) {
            ((BeeEntityAccessor) bee).invokeStartMovingTo(moobloom.getPosition());

            if (Core.debug)
                bee.addPotionEffect(new EffectInstance(Effects.GLOWING, 100));
        } else {

            // update bee tracking to take into account a moving moobloom
            if (moveTicks % 50 == 0)
                ((BeeEntityAccessor) bee).invokeStartMovingTo(moobloom.getPosition());

            double dist = bee.getPositionVec().distanceTo(moobloom.getPositionVec());
            if (dist < 2.2) {
                ((BeeEntityAccessor)bee).invokeSetHasNectar(false);

                if (Core.debug)
                    bee.removePotionEffect(Effects.GLOWING);

                moobloom.pollinate();
                moobloom = null;
            }
        }
    }
}

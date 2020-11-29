package svenhjol.charm.base.helper;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.mixin.accessor.GoalSelectorAccessor;
import svenhjol.charm.mixin.accessor.MobEntityAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

import static svenhjol.charm.base.helper.PosHelper.isLikeAir;
import static svenhjol.charm.base.helper.PosHelper.isLikeSolid;

public class MobHelper {
    public static Set<PrioritizedGoal> getGoals(MobEntity mob) {
        return ((GoalSelectorAccessor)getGoalSelector(mob)).getGoals();
    }

    public static GoalSelector getGoalSelector(MobEntity mob) {
        return ((MobEntityAccessor)mob).getGoalSelector();
    }

    public static <T extends Entity> T spawn(EntityType<T> type, ServerWorld world, BlockPos pos, SpawnReason reason) {
        return type.create(world, null, null, null, pos, reason, false, false);
    }

    public static void setEntityAttributes(EntityType<? extends LivingEntity> entityType, AttributeModifierMap attributes) {
        GlobalEntityTypeAttributes.put(entityType, attributes);
    }

    public static boolean spawnMobNearPos(ServerWorld world, BlockPos pos, MobEntity mob, BiConsumer<MobEntity, BlockPos> onSpawn) {
        int range = 6;
        int tries = 8;
        Random random = world.rand;
        List<BlockPos> validPositions = new ArrayList<>();
        int surface = world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());

        for (int y = surface; y < surface + range; y++) {
            for (int i = range; i > 1; --i) {
                for (int c = 1; c < tries; ++c) {
                    BlockPos checkPos = new BlockPos(pos.getX() + random.nextInt(i), y, pos.getZ() + random.nextInt(i));
                    BlockPos floor = checkPos.down();
                    BlockPos above = checkPos.up();
                    boolean areaIsValid = isLikeSolid(world, floor)
                        && isLikeAir(world, checkPos)
                        && isLikeAir(world, above);

                    if (areaIsValid)
                        validPositions.add(checkPos);

                    if (validPositions.size() > 2)
                        break;
                }
            }
        }

        if (validPositions.isEmpty()) {
            return false;
        } else {
            BlockPos spawnPos = validPositions.get(random.nextInt(validPositions.size()));
            mob.moveToBlockPosAndAngles(spawnPos, 0.0F, 0.0F);
            mob.onInitialSpawn(world, world.getDifficultyForLocation(spawnPos), SpawnReason.TRIGGERED, null, null);
            world.addEntity(mob);
            onSpawn.accept(mob, spawnPos);
            return true;
        }
    }
}

package svenhjol.charm.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.item.CharmItem;
import svenhjol.charm.mixin.accessor.PatrolSpawnerAccessor;
import svenhjol.charm.mixin.accessor.ServerWorldAccessor;
import svenhjol.charm.module.RaidHorns;

import java.util.List;
import java.util.Random;

@SuppressWarnings("NullableProblems")
public class RaidHornItem extends CharmItem {
    public RaidHornItem(CharmModule module) {
        super(module, "raid_horn", new Item.Properties()
            .maxStackSize(1)
            .maxDamage(16)
            .group(ItemGroup.MISC));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity user, Hand hand) {
        ItemStack horn = user.getHeldItem(hand);

        if (!world.isRemote) {
            world.playSound(null, user.getPosition(), CharmSounds.RAID_HORN, SoundCategory.PLAYERS, (float)RaidHorns.volume, 1.0F);
            user.setActiveHand(hand);
        }

        return ActionResult.resultConsume(horn);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isRemote)
            return;

        if (getPullProgress(getUseDuration(stack) - remainingUseTicks) < 1.0F)
            return;

        BlockPos pos = user.getPosition();
        ServerWorld serverWorld = (ServerWorld)world;
        if (serverWorld.hasRaid(pos)) {
            Raid raid = serverWorld.findRaid(pos);
            if (raid != null)
                raid.stop();
        } else {
            if (user instanceof PlayerEntity)
                trySpawnPillagers(serverWorld, (PlayerEntity)user);
        }

        if (user instanceof PlayerEntity)
            ((PlayerEntity)user).getCooldownTracker().setCooldown(this, 100);

        stack.damageItem(1, user, e -> e.sendBreakAnimation(EquipmentSlotType.MAINHAND));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    public static float getPullProgress(int useTicks) {
        float f = (float)useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    private void trySpawnPillagers(ServerWorld world, PlayerEntity player) {
        PatrolSpawner pillagerSpawner = null;
        List<ISpecialSpawner> spawners = ((ServerWorldAccessor)world).getSpawners();
        for (ISpecialSpawner spawner : spawners) {
            if (spawner instanceof PatrolSpawner) {
                pillagerSpawner = (PatrolSpawner)spawner;
                break;
            }
        }

        if (pillagerSpawner == null)
            return;

        Random random = world.getRandom();

        // copypasta from PatrolSpawner
        int j = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
        BlockPos.Mutable mutable = player.getPosition().toMutable().move(j, 0, k);
        if (!world.isAreaLoaded(mutable.getX() - 10, mutable.getY() - 10, mutable.getZ() - 10, mutable.getX() + 10, mutable.getY() + 10, mutable.getZ() + 10)) {
            return;
        } else {
            Biome biome = world.getBiome(mutable);
            Biome.Category category = biome.getCategory();
            if (category == Biome.Category.MUSHROOM) {
                return;
            } else {
                int m = 0;
                int n = (int)Math.ceil((double)world.getDifficultyForLocation(mutable).getAdditionalDifficulty()) + 1;

                for(int o = 0; o < n; ++o) {
                    ++m;
                    mutable.setY(world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, mutable).getY());
                    if (o == 0) {
                        if (!((PatrolSpawnerAccessor)pillagerSpawner).invokeSpawnPatroller(world, mutable, random, true)) {
                            break;
                        }
                    } else {
                        ((PatrolSpawnerAccessor)pillagerSpawner).invokeSpawnPatroller(world, mutable, random, false);
                    }

                    mutable.setX(mutable.getX() + random.nextInt(5) - random.nextInt(5));
                    mutable.setZ(mutable.getZ() + random.nextInt(5) - random.nextInt(5));
                }

                // must reset the global pillager spawner timer after spawning these in
                ((PatrolSpawnerAccessor)pillagerSpawner).setTicksUntilNextSpawn(12000);
                return;
            }
        }
    }
}

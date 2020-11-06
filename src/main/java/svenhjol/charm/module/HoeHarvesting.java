package svenhjol.charm.module;

import com.mojang.brigadier.StringReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Right-click with a hoe to quickly harvest and replant a fully-grown crop.")
public class HoeHarvesting extends CharmModule {
    private static final List<BlockState> harvestable = new ArrayList<>();

    @Override
    public void init() {
        addHarvestable("minecraft:beetroots[age=3]");
        addHarvestable("minecraft:carrots[age=7]");
        addHarvestable("minecraft:nether_wart[age=3]");
        addHarvestable("minecraft:potatoes[age=7]");
        addHarvestable("minecraft:wheat[age=7]");

        UseBlockCallback.EVENT.register(this::tryHarvest);
    }

    public ActionResult tryHarvest(PlayerEntity player, World world, Hand hand, BlockRayTraceResult hitResult) {
        ItemStack held = player.getHeldItem(hand);
        BlockPos pos = hitResult.getPos();

        if (!world.isRemote && held.getItem() instanceof HoeItem) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            ServerWorld serverWorld = (ServerWorld)serverPlayer.world;
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!harvestable.contains(state))
                return ActionResult.PASS;

            Item blockItem = block.asItem();
            BlockState newState = block.getDefaultState();

            List<ItemStack> drops = Block.getDroppedStacks(state, serverWorld, pos, null, player, ItemStack.EMPTY);
            for (ItemStack drop : drops) {
                if (drop.getItem() == blockItem)
                    drop.decrement(1);

                if (!drop.isEmpty())
                    Block.dropStack(world, pos, drop);
            }

            world.playEvent(2001, pos, Block.getStateId(newState));
            world.setBlockState(pos, newState);

            // damage the hoe a bit
            held.damageItem(1, player, p -> p.swingArm(hand));
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    public static void addHarvestable(String blockState) {
        BlockState state;

        try {
            BlockArgumentParser parser = new BlockArgumentParser(new StringReader(blockState), false).parse(false);
            state = parser.getBlockState();
        } catch (Exception e) {
            state = null;
        }

        if (state == null)
            state = Blocks.AIR.getDefaultState();

        harvestable.add(state);
    }
}

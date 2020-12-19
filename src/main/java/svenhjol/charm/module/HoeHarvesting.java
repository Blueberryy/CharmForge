package svenhjol.charm.module;

import com.mojang.brigadier.StringReader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Right-click with a hoe to quickly harvest and replant a fully-grown crop.", hasSubscriptions = true)
public class HoeHarvesting extends CharmModule {
    private static final List<BlockState> harvestable = new ArrayList<>();

    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:tweaks.module.hoe_harvesting_module") || override;
    }

    @Override
    public void init() {
        addHarvestable("minecraft:beetroots[age=3]");
        addHarvestable("minecraft:carrots[age=7]");
        addHarvestable("minecraft:nether_wart[age=3]");
        addHarvestable("minecraft:potatoes[age=7]");
        addHarvestable("minecraft:wheat[age=7]");
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled()) {
            boolean result = tryHarvest(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos());
            event.setCanceled(result);
        }
    }

    public boolean tryHarvest(PlayerEntity player, World world, Hand hand, BlockPos pos) {
        ItemStack held = player.getHeldItem(hand);

        if (!world.isRemote && held.getItem() instanceof HoeItem) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
            ServerWorld serverWorld = (ServerWorld)serverPlayer.world;
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if (!harvestable.contains(state))
                return false;

            Item blockItem = block.asItem();
            BlockState newState = block.getDefaultState();

            List<ItemStack> drops = Block.getDrops(state, serverWorld, pos, null, player, ItemStack.EMPTY);
            for (ItemStack drop : drops) {
                if (drop.getItem() == blockItem)
                    drop.shrink(1);

                if (!drop.isEmpty())
                    Block.spawnAsEntity(world, pos, drop);
            }

            world.playEvent(2001, pos, Block.getStateId(newState));
            world.setBlockState(pos, newState);
            world.playSound(null, pos, SoundEvents.BLOCK_CROP_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);

            // damage the hoe a bit
            held.damageItem(1, player, p -> p.swingArm(hand));
            return true;
        }

        return false;
    }

    public static void addHarvestable(String blockState) {
        BlockState state;

        try {
            BlockStateParser parser = new BlockStateParser(new StringReader(blockState), false).parse(false);
            state = parser.getState();
        } catch (Exception e) {
            state = null;
        }

        if (state == null)
            state = Blocks.AIR.getDefaultState();

        harvestable.add(state);
    }
}

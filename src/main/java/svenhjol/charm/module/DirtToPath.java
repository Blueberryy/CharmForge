package svenhjol.charm.module;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Right-clicking dirt with a shovel turns it into grass path.", hasSubscriptions = true)
public class DirtToPath extends CharmModule {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled()) {
            boolean result = convertDirt(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos());
            if (result)
                event.setCanceled(true);
        }
    }

    private boolean convertDirt(PlayerEntity player, World world, Hand hand, BlockPos pos) {
        ItemStack stack = player.getHeldItem(hand);

        if (world != null && stack.getItem() instanceof ShovelItem) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.DIRT) {
                player.swingArm(hand);

                if (!world.isRemote) {
                    world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState(), 11);
                    world.playSound(null, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // damage the shovel a bit
                    stack.damageItem(1, player, p -> p.swingArm(hand));
                    return true;
                }
            }
        }
        return false;
    }
}

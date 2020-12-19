package svenhjol.charm.module;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Right-clicking on a grass path block with a hoe turns it back into dirt.", hasSubscriptions = true)
public class PathToDirt extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:tweaks.module.dirt_to_path_module") || override;
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled()) {
            boolean result = convertPath(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos());
            event.setCanceled(result);
        }
    }

    private boolean convertPath(PlayerEntity player, World world, Hand hand, BlockPos pos) {
        ItemStack stack = player.getHeldItem(hand);

        if (world != null && stack.getItem() instanceof HoeItem) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.GRASS_PATH) {
                player.swingArm(hand);

                if (!world.isRemote) {
                    world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 11);
                    world.playSound(null, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

                    // damage the hoe a bit
                    stack.damageItem(1, player, p -> p.swingArm(hand));
                    return true;
                }
            }
        }
        return false;
    }
}

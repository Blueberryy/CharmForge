package svenhjol.charm.module;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.PosHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.PlacedGlowstoneDustBlock;

@Module(mod = Charm.MOD_ID, description = "Glowstone dust can be placed on the ground as a light source.", hasSubscriptions = true)
public class PlaceableGlowstoneDust extends CharmModule {
    public static PlacedGlowstoneDustBlock PLACED_GLOWSTONE_DUST;

    @Override
    public void register() {
        PLACED_GLOWSTONE_DUST = new PlacedGlowstoneDustBlock(this);
    }

    @Override
    public void clientInit() {
        RenderTypeLookup.setRenderLayer(PLACED_GLOWSTONE_DUST, RenderType.getCutout());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.isCanceled()) {
            boolean result = tryPlaceDust(event.getPlayer(), event.getWorld(), event.getPos(), event.getFace(), event.getHand());
            event.setCanceled(result);
        }
    }

    public static boolean tryPlaceDust(World world, RayTraceResult hitResult) {
        if (hitResult.getType() != RayTraceResult.Type.BLOCK)
            return false;

        BlockRayTraceResult BlockRayTraceResult = (BlockRayTraceResult)hitResult;
        BlockPos pos = BlockRayTraceResult.getPos();
        Direction side = BlockRayTraceResult.getFace();

        return tryPlaceDust(world, pos, side);
    }

    public static boolean tryPlaceDust(World world, BlockPos pos, Direction side) {
        BlockState state = world.getBlockState(pos);

        BlockPos offsetPos = pos.offset(side);

        if (state.isSolidSide(world, pos, side) && PosHelper.isLikeAir(world, offsetPos) && world.getBlockState(offsetPos).getBlock() != Blocks.LAVA) {
            BlockState placedState = PlaceableGlowstoneDust.PLACED_GLOWSTONE_DUST.getDefaultState()
                .with(PlacedGlowstoneDustBlock.FACING, side);

            BlockState offsetState = world.getBlockState(offsetPos);
            if (offsetState.getBlock() == Blocks.WATER)
                placedState = placedState.with(BlockStateProperties.WATERLOGGED, true);

            world.setBlockState(offsetPos, placedState, 2);
            world.playSound(null, offsetPos, SoundEvents.BLOCK_NYLIUM_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        }

        return false;
    }

    private boolean tryPlaceDust(PlayerEntity player, World world, BlockPos pos, Direction side, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world != null && stack.getItem() == Items.GLOWSTONE_DUST) {
            player.swingArm(hand);

            if (!world.isRemote) {
                boolean result = tryPlaceDust(world, pos, side);

                if (result) {
                    if (!player.isCreative())
                        stack.shrink(1);

                    return true;
                }
                return false;
            }
        }

        return false;
    }
}

package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.EnchantmentsHelper;
import svenhjol.charm.base.helper.PlayerHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.enchantment.AcquisitionEnchantment;

import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Tools with the Acquisition enchantment automatically pick up drops.")
public class Acquisition extends CharmModule {
    public static AcquisitionEnchantment ACQUISITION;

    @Override
    public void register() {
        ACQUISITION = new AcquisitionEnchantment(this);
    }

    public static boolean tryOverrideBreakBlock(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, TileEntity blockEntity) {
        ItemStack held = player.getHeldItemMainhand();

        if (!EnchantmentsHelper.has(held, ACQUISITION))
            return false;

        List<ItemStack> dropped = Block.getDrops(state, world, pos, blockEntity, player, held);
        dropped.forEach(drop -> PlayerHelper.addOrDropStack(player, drop));

        state.spawnAdditionalDrops(world, pos, held);

        return true;
    }
}

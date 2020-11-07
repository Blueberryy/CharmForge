package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.container.WoodcutterContainer;

import javax.annotation.Nullable;

public class WoodcutterBlock extends StonecutterBlock implements ICharmBlock {
    private CharmModule module;
    private static final ITextComponent TITLE = new TranslationTextComponent("container.charm.woodcutter");

    public WoodcutterBlock(CharmModule module) {
        super(AbstractBlock.Properties
            .from(Blocks.STONECUTTER)
            .harvestTool(ToolType.AXE));

        register(module, "woodcutter");
        this.module = module;
    }

    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            player.openContainer(state.getContainer(world, pos));
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        if (enabled())
            super.fillItemGroup(group, list);
    }

    @Override
    @Nullable
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider((i, playerInventory, playerEntity)
            -> new WoodcutterContainer(i, playerInventory, IWorldPosCallable.of(world, pos)), TITLE);
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }
}

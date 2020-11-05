package svenhjol.charm.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.TileEntity;
import net.minecraft.block.entity.ChestTileEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.collection.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import svenhjol.charm.TileEntity.VariantTrappedChestTileEntity;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class VariantTrappedChestBlock extends ChestBlock implements ICharmBlock, IVariantChestBlock {
    private final CharmModule module;
    private final IVariantMaterial type;

    public VariantTrappedChestBlock(CharmModule module, IVariantMaterial type) {
        super(Settings.copy(Blocks.TRAPPED_CHEST), () -> VariantChests.TRAPPED_BLOCK_ENTITY);

        this.module = module;
        this.type = type;

        this.register(module, type.asString() + "_trapped_chest");
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.REDSTONE;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (enabled())
            super.fillItemGroup(group, items);
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(IBlockReader worldIn) {
        VariantTrappedChestTileEntity chest = new VariantTrappedChestTileEntity();
        chest.setCustomName(new TranslationTextComponent("block." + module.mod + "." + type.asString() + "_trapped_chest"));

        return chest;
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return MathHelper.clamp(ChestTileEntity.getPlayersLookingInChestCount(world, pos), 0, 15);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? state.getWeakPower(world, pos, direction) : 0;
    }

    @Override
    public IVariantMaterial getMaterialType() {
        return type;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    /**
     * Copypasta from {@link net.minecraft.block.TrappedChestBlock}
     */
    protected Stat<ResourceLocation> getOpenStat() {
        return Stats.CUSTOM.getOrCreateStat(Stats.TRIGGER_TRAPPED_CHEST);
    }
}

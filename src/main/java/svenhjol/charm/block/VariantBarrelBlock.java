package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BarrelTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.mixin.accessor.BarrelTileEntityAccessor;
import svenhjol.charm.module.VariantBarrels;

import javax.annotation.Nullable;

public class VariantBarrelBlock extends BarrelBlock implements ICharmBlock {
    protected CharmModule module;
    protected IVariantMaterial type;

    public VariantBarrelBlock(CharmModule module, IVariantMaterial type) {
        super(AbstractBlock.Properties.from(Blocks.BARREL));

        this.module = module;
        this.type = type;

        this.register(module, type.getString() + "_barrel");
        this.setDefaultState(this.getStateContainer()
            .getBaseState()
            .with(PROPERTY_FACING, Direction.NORTH)
            .with(PROPERTY_OPEN, false)
        );
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.DECORATIONS;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list) {
        if (enabled())
            super.fillItemGroup(group, list);
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return BarrelTileEntityAccessor.invokeConstructor(VariantBarrels.BLOCK_ENTITY);
    }
}

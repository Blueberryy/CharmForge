package svenhjol.charm.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.tileentity.VariantChestTileEntity;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class VariantChestBlock extends ChestBlock implements ICharmBlock, IVariantChestBlock {
    private final CharmModule module;
    private final IVariantMaterial type;

    public VariantChestBlock(CharmModule module, IVariantMaterial type) {
        super(Properties.from(Blocks.CHEST), () -> VariantChests.NORMAL_BLOCK_ENTITY);

        this.module = module;
        this.type = type;

        this.register(module, type.getString() + "_chest");
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.DECORATIONS;
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
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        VariantChestTileEntity chest = new VariantChestTileEntity();
        chest.setCustomName(new TranslationTextComponent("block." + module.mod + "." + type.getString() + "_chest"));
        return chest;
    }

    @Override
    public IVariantMaterial getMaterialType() {
        return this.type;
    }
}

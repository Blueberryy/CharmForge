package svenhjol.charm.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.TileEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.NonNullList;
import net.minecraft.world.BlockView;
import svenhjol.charm.TileEntity.VariantChestTileEntity;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class VariantChestBlock extends ChestBlock implements ICharmBlock, IVariantChestBlock {
    private final CharmModule module;
    private final IVariantMaterial type;

    public VariantChestBlock(CharmModule module, IVariantMaterial type) {
        super(Settings.copy(Blocks.CHEST), () -> VariantChests.NORMAL_BLOCK_ENTITY);

        this.module = module;
        this.type = type;

        this.register(module, type.asString() + "_chest");
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
    public TileEntity createTileEntity(BlockView worldIn) {
        VariantChestTileEntity chest = new VariantChestTileEntity();
        chest.setCustomName(new TranslatableText("block." + module.mod + "." + type.asString() + "_chest"));
        return chest;
    }

    @Override
    public IVariantMaterial getMaterialType() {
        return this.type;
    }
}

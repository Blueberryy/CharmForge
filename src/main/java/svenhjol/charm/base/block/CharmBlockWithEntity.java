package svenhjol.charm.base.block;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;

public abstract class CharmBlockWithEntity extends CharmBlock implements ICharmBlock {
    public CharmModule module;

    protected CharmBlockWithEntity(CharmModule module, String name, Properties props) {
        super(module, name, props);
        this.module = module;
        register(module, name);
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

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

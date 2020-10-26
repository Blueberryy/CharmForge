package svenhjol.charm.base.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;

public abstract class CharmBlock extends Block implements ICharmBlock {
    public CharmModule module;

    public CharmBlock(CharmModule module, String name, AbstractBlock.Properties props) {
        super(props);
        this.module = module;
        register(module, name);
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
}

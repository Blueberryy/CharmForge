package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;

public abstract class BaseLanternBlock extends LanternBlock implements ICharmBlock {
    protected CharmModule module;

    public BaseLanternBlock(CharmModule module, String name, AbstractBlock.Properties settings) {
        super(settings);
        this.module = module;
        register(module, name);
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
}

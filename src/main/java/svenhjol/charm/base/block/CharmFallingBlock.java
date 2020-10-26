package svenhjol.charm.base.block;

import net.minecraft.block.FallingBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;

@SuppressWarnings({"NullableProblems", "unused"})
public abstract class CharmFallingBlock extends FallingBlock implements ICharmFallingBlock {
    protected CharmModule module;

    public CharmFallingBlock(CharmModule module, String name, Properties props) {
        super(props);
        this.module = module;
        register(module, name);
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.BUILDING_BLOCKS;
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

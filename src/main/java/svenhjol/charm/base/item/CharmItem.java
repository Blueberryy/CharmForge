package svenhjol.charm.base.item;

import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmModule;

public abstract class CharmItem extends Item implements ICharmItem {
    protected CharmModule module;

    public CharmItem(CharmModule module, String name, Item.Properties props) {
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

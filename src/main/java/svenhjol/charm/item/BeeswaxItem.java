package svenhjol.charm.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.item.CharmItem;

public class BeeswaxItem extends CharmItem {
    public BeeswaxItem(CharmModule module) {
        super(module, "beeswax", new Item.Properties()
            .group(ItemGroup.MATERIALS));
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 800;
    }
}

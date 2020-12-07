package svenhjol.charm.base;

import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.helper.LootHelper;

public class CharmLoot {
    public static ResourceLocation VILLAGE_LIBRARIAN = new ResourceLocation(Charm.MOD_ID, "chests/village_librarian");

    public static void init() {
        LootHelper.CUSTOM_LOOT_TABLES.add(VILLAGE_LIBRARIAN);
    }
}

package svenhjol.charm.base.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;

public interface ICharmItem {
    boolean enabled();

    default void register(CharmModule module, String name) {
        RegistryHandler.item(new ResourceLocation(module.mod, name), (Item)this);
    }
}

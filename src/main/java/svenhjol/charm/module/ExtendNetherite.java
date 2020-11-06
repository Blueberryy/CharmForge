package svenhjol.charm.module;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Extends the lifetime of netherite-based items before they despawn.", hasSubscriptions = true)
public class ExtendNetherite extends CharmModule {
    @Config(name = "Extra lifetime", description = "Additional time (in seconds) given to netherite and netherite-based items before they despawn.")
    public static int extendBy = 300;

    public static List<Item> netheriteItems = new ArrayList<>();

    @Override
    public void init() {
        netheriteItems = Arrays.asList(
            Items.NETHERITE_AXE,
            Items.NETHERITE_BRICKS,
            Items.NETHERITE_BOOTS,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_HOE,
            Items.NETHERITE_INGOT,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_SCRAP,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_SWORD,
            NetheriteNuggets.NETHERITE_NUGGET
        );
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        if (!event.isCanceled()) {
            Item item = event.getEntityItem().getItem().getItem();

            if (netheriteItems.contains(item)) {
                if (event.getEntityItem().getAge() <= 6020) { // 6000 is default lifetime, add a little buffer just in case
                    event.setExtraLife(extendBy * 20); // in ticks
                    event.setCanceled(true);
                }
            }
        }
    }
}

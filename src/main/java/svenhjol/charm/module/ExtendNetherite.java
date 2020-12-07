package svenhjol.charm.module;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.mixin.accessor.ItemEntityAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Extends the lifetime of netherite-based items before they despawn.", hasSubscriptions = true)
public class ExtendNetherite extends CharmModule {
    @Config(name = "Extra lifetime", description = "Additional time (in seconds) given to netherite and netherite-based items before they despawn.")
    public static int extendBy = 300;

    public static List<Item> netheriteItems = new ArrayList<>();

    private static ItemStack lastTossedStack = null;
    private static long lastTossedTime = 0;

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
    public void onItemToss(ItemTossEvent event) {
        // Forge fires this event when using /give.
        // Capture the item and time it was given so that when it subsequently fires
        // the ItemExpireEvent (don't ask) we can ignore it.
        World world = event.getEntity().getEntityWorld();
        if (!world.isRemote) {
            lastTossedStack = event.getEntityItem().getItem();
            lastTossedTime = world.getGameTime();
        }
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        if (!event.isCanceled()) {
            World world = event.getEntity().getEntityWorld();

            if (world.isRemote)
                return;

            ItemStack stack = event.getEntityItem().getItem();
            long gameTime = world.getGameTime();
            long existedTicks = gameTime - lastTossedTime;

            // Hack: if the item has existed for less than 5 ticks, don't add extra life to it. #395
            if (lastTossedStack != null && ItemStack.areItemsEqual(stack, lastTossedStack) && existedTicks >= 0 && existedTicks < 5) {
                lastTossedStack = null;
                lastTossedTime = 0;
                return;
            }

            int age = ((ItemEntityAccessor) event.getEntityItem()).getAge();

            if (netheriteItems.contains(stack.getItem()) && age <= 6020) { // 6000 is default lifetime, add a little buffer just in case
                event.setExtraLife(extendBy * 20); // in ticks
                event.setCanceled(true);
            }
        }
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.AtlasClient;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.item.AtlasItem;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

@Module(mod = Charm.MOD_ID, client = AtlasClient.class, description = "A map storage." /*TODO describe better*/, hasSubscriptions = true)
public class Atlas extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "atlas");
    // add items to this list to whitelist them in atlases
    public static final List<Item> VALID_ATLAS_ITEMS = new ArrayList<>();
    private static final WeakHashMap<ItemStack, AtlasInventory> cache = new WeakHashMap<>();

    @Config(name = "Map Size", description = "The atlas will create maps of this size (0-4).")
    public static int mapSize = 2;

    public static AtlasItem ATLAS_ITEM;
    public static ContainerType<AtlasContainer> CONTAINER;

    public static boolean canAtlasInsertItem(ItemStack stack) {
        return VALID_ATLAS_ITEMS.contains(stack.getItem());
    }

    public static boolean inventoryContainsMap(PlayerInventory inventory, ItemStack itemStack) {
        if (inventory.hasItemStack(itemStack)) {
            return true;
        } else if (ModuleHandler.enabled(Atlas.class)) {
            for (Hand hand : Hand.values()) {
                ItemStack atlasStack = inventory.player.getHeldItem(hand);
                if (atlasStack.getItem() == ATLAS_ITEM) {
                    AtlasInventory inv = getInventory(inventory.player.world, atlasStack);
                    if (inv.hasItemStack(itemStack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static AtlasInventory getInventory(World world, ItemStack stack) {
        AtlasInventory inventory = cache.get(stack);
        if (inventory == null) {
            inventory = new AtlasInventory(world, stack, stack.getDisplayName());
            cache.put(stack, inventory);
        }
        return inventory;
    }

    @Override
    public void register() {
        ATLAS_ITEM = new AtlasItem(this);

        VALID_ATLAS_ITEMS.add(Items.MAP);
        VALID_ATLAS_ITEMS.add(Items.FILLED_MAP);

        CONTAINER = RegistryHandler.container(ID, AtlasContainer::new);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            for (Hand hand : Hand.values()) {
                ItemStack atlasStack = player.getHeldItem(hand);
                if (atlasStack.getItem() == ATLAS_ITEM) {
                    AtlasInventory inventory = getInventory(player.world, atlasStack);
                    AtlasInventory.MapInfo mapInfo = inventory.updateActiveMap(player);
                    if (mapInfo != null) {
                        ItemStack map = inventory.getStackInSlot(mapInfo.slot);
                        sendMapToClient(player, map, mapInfo.slot);
                    }
                }
            }
        }
    }

    public static void sendMapToClient(ServerPlayerEntity player, ItemStack map, int slot) {
        if (map.getItem().isComplex()) {
            map.getItem().inventoryTick(map, player.world, player, slot, true);
            IPacket<?> packet = ((AbstractMapItem) map.getItem()).getUpdatePacket(map, player.world, player);
            if (packet != null) {
                player.connection.sendPacket(packet);
            }
        }
    }
}

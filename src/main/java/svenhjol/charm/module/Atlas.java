package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
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
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.AtlasClient;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.item.AtlasItem;
import svenhjol.charm.message.ServerAtlasTransfer;

import java.util.*;

@Module(mod = Charm.MOD_ID, client = AtlasClient.class, description = "Storage for maps that automatically updates the displayed map as you explore.", hasSubscriptions = true)
public class Atlas extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "atlas");
    // add items to this list to whitelist them in atlases
    public static final List<Item> VALID_ATLAS_ITEMS = new ArrayList<>();
    private static final Map<UUID, AtlasInventory> cache = new HashMap<>();

    @Config(name = "Open in off hand", description = "Allow opening the atlas while it is in the off hand")
    public static boolean offHandOpen = false;

    public static AtlasItem ATLAS_ITEM;
    public static ContainerType<AtlasContainer> CONTAINER;

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
        CompoundNBT nbt = ItemNBTHelper.getNBT(stack);
        UUID id;
        if (nbt.hasUniqueId(AtlasInventory.ID)) {
            id = nbt.getUniqueId(AtlasInventory.ID);
        } else {
            id = UUID.randomUUID();
            nbt.putUniqueId(AtlasInventory.ID, id);
        }
        AtlasInventory inventory = cache.get(id);
        if (inventory == null) {
            inventory = new AtlasInventory(world, stack);
            cache.put(id, inventory);
        }
        return inventory;
    }

    public static void sendMapToClient(ServerPlayerEntity player, ItemStack map) {
        if (map.getItem().isComplex()) {
            map.getItem().inventoryTick(map, player.world, player, -1, true);
            IPacket<?> packet = ((AbstractMapItem) map.getItem()).getUpdatePacket(map, player.world, player);
            if (packet != null) {
                player.connection.sendPacket(packet);
            }
        }
    }

    public static void serverCallback(ServerPlayerEntity player, ServerAtlasTransfer msg) {
        AtlasInventory inventory = Atlas.getInventory(player.world, player.inventory.getStackInSlot(msg.atlasSlot));
        switch (msg.mode) {
            case TO_HAND:
                player.inventory.setItemStack(inventory.removeMapByCoords(msg.mapX, msg.mapZ).map);
                player.updateHeldItem();
                break;
            case TO_INVENTORY:
                player.addItemStackToInventory(inventory.removeMapByCoords(msg.mapX, msg.mapZ).map);
                break;
            case FROM_HAND:
                ItemStack heldItem = player.inventory.getItemStack();
                if (heldItem.getItem() == Items.FILLED_MAP && FilledMapItem.getMapData(heldItem, player.world).scale == inventory.getScale()) {
                    inventory.addToInventory(heldItem);
                    player.inventory.setItemStack(ItemStack.EMPTY);
                    player.updateHeldItem();
                }
                break;
            case FROM_INVENTORY:
                ItemStack stack = player.inventory.getStackInSlot(msg.mapX);
                if (stack.getItem() == Items.FILLED_MAP && FilledMapItem.getMapData(stack, player.world).scale == inventory.getScale()) {
                    inventory.addToInventory(stack);
                    player.inventory.removeStackFromSlot(msg.mapX);
                }
                break;
        }
    }

    private static AtlasInventory findAtlas(PlayerInventory inventory) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = inventory.player.getHeldItem(hand);
            if (stack.getItem() == Atlas.ATLAS_ITEM) {
                return getInventory(inventory.player.world, stack);
            }
        }
        throw new IllegalStateException("No atlas in any hand, can't open!");
    }

    @Override
    public void register() {
        ATLAS_ITEM = new AtlasItem(this);

        VALID_ATLAS_ITEMS.add(Items.MAP);
        VALID_ATLAS_ITEMS.add(Items.FILLED_MAP);

        CONTAINER = RegistryHandler.container(ID, (syncId, playerInventory)-> new AtlasContainer(syncId, playerInventory, findAtlas(playerInventory)));
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
                        sendMapToClient(player, mapInfo.map);
                    }
                }
            }
        }
    }
}

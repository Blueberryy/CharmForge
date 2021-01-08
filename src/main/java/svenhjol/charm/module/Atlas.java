package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
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
import svenhjol.charm.message.ClientUpdateAtlasInventory;
import svenhjol.charm.message.ServerAtlasTransfer;

import java.util.*;

@Module(mod = Charm.MOD_ID, client = AtlasClient.class, description = "Storage for maps that automatically updates the displayed map as you explore.", hasSubscriptions = true)
public class Atlas extends CharmModule {
    public static final ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "atlas");
    // add items to this list to whitelist them in atlases
    public static final List<Item> VALID_ATLAS_ITEMS = new ArrayList<>();
    private static final Map<UUID, AtlasInventory> serverCache = new HashMap<>();
    private static final Map<UUID, AtlasInventory> clientCache = new HashMap<>();

    @Config(name = "Open in off hand", description = "Allow opening the atlas while it is in the off hand.")
    public static boolean offHandOpen = false;

    @Config(name = "Map scale", description = "Map scale used in atlases by default.")
    public static int defaultScale = 1;

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
        UUID id = ItemNBTHelper.getUuid(stack, AtlasInventory.ID);
        if (id == null) {
            id = UUID.randomUUID();
            ItemNBTHelper.setUuid(stack, AtlasInventory.ID, id);
        }
        Map<UUID, AtlasInventory> cache = world.isRemote ? clientCache : serverCache;
        AtlasInventory inventory = cache.get(id);
        if (inventory == null) {
            inventory = new AtlasInventory(stack);
            cache.put(id, inventory);
        }
        if(inventory.getAtlasItem() != stack) {
            inventory.reload(stack);
        }
        return inventory;
    }

    public static void sendMapToClient(ServerPlayerEntity player, ItemStack map, boolean markDirty) {
        if (map.getItem().isComplex()) {
            if(markDirty) {
                FilledMapItem.getMapData(map, player.world).updateMapData(0, 0);
            }
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
                player.inventory.setItemStack(inventory.removeMapByCoords(player.world, msg.mapX, msg.mapZ).map);
                player.updateHeldItem();
                updateClient(player, msg.atlasSlot);
                break;
            case TO_INVENTORY:
                player.addItemStackToInventory(inventory.removeMapByCoords(player.world, msg.mapX, msg.mapZ).map);
                updateClient(player, msg.atlasSlot);
                break;
            case FROM_HAND:
                ItemStack heldItem = player.inventory.getItemStack();
                if (heldItem.getItem() == Items.FILLED_MAP && FilledMapItem.getMapData(heldItem, player.world).scale == inventory.getScale()) {
                    inventory.addToInventory(player.world, heldItem);
                    player.inventory.setItemStack(ItemStack.EMPTY);
                    player.updateHeldItem();
                    updateClient(player, msg.atlasSlot);
                }
                break;
            case FROM_INVENTORY:
                ItemStack stack = player.inventory.getStackInSlot(msg.mapX);
                if (stack.getItem() == Items.FILLED_MAP && FilledMapItem.getMapData(stack, player.world).scale == inventory.getScale()) {
                    inventory.addToInventory(player.world, stack);
                    player.inventory.removeStackFromSlot(msg.mapX);
                    updateClient(player, msg.atlasSlot);
                }
                break;
        }
    }

    public static void updateClient(ServerPlayerEntity player, int atlasSlot) {
        Charm.PACKET_HANDLER.sendToPlayer(new ClientUpdateAtlasInventory(atlasSlot), player);
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

    public static void setupAtlasUpscale(PlayerInventory playerInventory, CartographyContainer container) {
        if (ModuleHandler.enabled(Atlas.class)) {
            Slot oldSlot = container.inventorySlots.get(0);
            container.inventorySlots.set(0, new Slot(oldSlot.inventory, oldSlot.getSlotIndex(), oldSlot.xPos, oldSlot.yPos) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    return oldSlot.isItemValid(stack) || stack.getItem() == ATLAS_ITEM && getInventory(playerInventory.player.world, stack).getMapInfos().isEmpty();
                }
            });
        }
    }

    public static boolean makeAtlasUpscaleOutput(ItemStack topStack, ItemStack bottomStack, ItemStack outputStack, World world,
                                                 CraftResultInventory craftResultInventory, CartographyContainer cartographyContainer) {
        if (ModuleHandler.enabled(Atlas.class) && topStack.getItem() == Atlas.ATLAS_ITEM) {
            AtlasInventory inventory = Atlas.getInventory(world, topStack);
            ItemStack output;
            if (inventory.getMapInfos().isEmpty() && bottomStack.getItem() == Items.MAP && inventory.getScale() < 4) {
                output = topStack.copy();
                ItemNBTHelper.setUuid(output, AtlasInventory.ID, UUID.randomUUID());
                ItemNBTHelper.setInt(output, AtlasInventory.SCALE, inventory.getScale() + 1);
            } else {
                output = ItemStack.EMPTY;
            }
            if (!ItemStack.areItemStacksEqual(output, outputStack)) {
                craftResultInventory.setInventorySlotContents(2, output);
                cartographyContainer.detectAndSendChanges();
            }
            return true;
        }
        return false;
    }

    @Override
    public void register() {
        ATLAS_ITEM = new AtlasItem(this);

        VALID_ATLAS_ITEMS.add(Items.MAP);
        VALID_ATLAS_ITEMS.add(Items.FILLED_MAP);

        CONTAINER = RegistryHandler.container(ID, (syncId, playerInventory) -> new AtlasContainer(syncId, playerInventory, findAtlas(playerInventory)));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && event.side == LogicalSide.SERVER) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            for (Hand hand : Hand.values()) {
                ItemStack atlas = player.getHeldItem(hand);
                if (atlas.getItem() == ATLAS_ITEM) {
                    AtlasInventory inventory = getInventory(player.world, atlas);
                    if (inventory.updateActiveMap(player)) {
                        updateClient(player, getSlotFromHand(player, hand));
                    }
                }
            }
        }
    }

    private static int getSlotFromHand(PlayerEntity player, Hand hand) {
        if(hand == Hand.MAIN_HAND) {
            return player.inventory.currentItem;
        } else {
            return player.inventory.getSizeInventory() - 1;
        }
    }
}

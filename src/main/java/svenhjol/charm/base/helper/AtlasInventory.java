package svenhjol.charm.base.helper;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.container.AtlasContainer;
import svenhjol.charm.module.Atlas;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class AtlasInventory implements INamedContainerProvider, IInventory {
    public static String CONTENTS = "contents";
    public static String ACTIVE_MAP = "active_map";
    public static int SIZE = 18;

    private final NonNullList<ItemStack> items;
    private final World world;
    private final ItemStack itemStack;
    private final ITextComponent name;
    private List<MapInfo> mapInfos = new ArrayList<>();

    public AtlasInventory(World world, ItemStack itemStack, ITextComponent name) {
        this.world = world;
        this.itemStack = itemStack;
        this.name = name;
        this.items = getInventory(itemStack);
        updateMapInfos();
    }

    private static NonNullList<ItemStack> getInventory(ItemStack itemStack) {
        CompoundNBT nbt = ItemNBTHelper.getCompound(itemStack, CONTENTS);
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, items);
        return items;
    }

    private void updateMapInfos() {
        if (!world.isRemote) {
            mapInfos = items.stream().filter(stack -> stack.getItem() == Items.FILLED_MAP).map(stack -> {
                int id = FilledMapItem.getMapId(stack);
                MapData mapData = FilledMapItem.getMapData(stack, world);
                if (mapData == null) {
                    return null;
                } else {
                    return new MapInfo(mapData.xCenter, mapData.zCenter, 64 * (1 << mapData.scale), id);
                }
            }).filter(Objects::nonNull).sorted(Comparator.comparingInt(info -> info.scale)).collect(Collectors.toList());
        }
    }

    public void updateActiveMap(ServerPlayerEntity player) {
        int x = (int) Math.floor(player.getPosX());
        int z = (int) Math.floor(player.getPosZ());
        int activeMap = mapInfos.stream()
                .filter(info -> x >= info.x - info.scale && x < info.x + info.scale && z >= info.z - info.scale && z < info.z + info.scale)
                .findFirst().map(it -> it.id).orElse(-1);
        if(activeMap == -1) {
            activeMap = makeNewMap(player, x, z);
        }
        ItemNBTHelper.setInt(itemStack, ACTIVE_MAP, activeMap);
    }

    private int makeNewMap(ServerPlayerEntity player, int x, int z) {
        int emptySlot = -1;
        for(int i = 0; i < getSizeInventory(); ++i) {
            ItemStack stack = getStackInSlot(i);
            if(stack.isEmpty()) {
                emptySlot = i;
                break;
            }
        }
        if(emptySlot != -1) {
            for (int i = 0; i < getSizeInventory(); ++i) {
                ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == Items.MAP) {
                    if(!player.isCreative()) {
                        decrStackSize(i, 1);
                    }
                    ItemStack map = FilledMapItem.setupNewMap(world, x, z, (byte) Atlas.mapSize, true, true);
                    setInventorySlotContents(emptySlot, map);
                    return FilledMapItem.getMapId(map);
                }
            }
        }
        return -1;
    }

    @Nullable
    public MapData getActiveMap(ClientPlayerEntity player) {
        int activeId = ItemNBTHelper.getInt(itemStack, ACTIVE_MAP, -1);
        if(activeId == -1) return null;
        return player.world.getMapData(FilledMapItem.getMapName(activeId));
    }

    @Override
    public ITextComponent getDisplayName() {
        return name;
    }

    @Nullable
    @Override
    public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AtlasContainer(syncId, playerInventory, this);
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return items.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(items, index, count);
        markDirty();
        return itemstack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = ItemStackHelper.getAndRemove(items, index);
        markDirty();
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        items.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }

        this.markDirty();
    }

    @Override
    public void markDirty() {
        updateMapInfos();
        CompoundNBT nbt = new CompoundNBT();
        ItemStackHelper.saveAllItems(nbt, items, false);
        ItemNBTHelper.setCompound(itemStack, CONTENTS, nbt);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            if (player.getHeldItem(hand) == itemStack) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        items.clear();
    }

    private static class MapInfo {
        public final double x;
        public final double z;
        public final int scale;
        public final int id;

        private MapInfo(double x, double z, int scale, int id) {
            this.x = x;
            this.z = z;
            this.scale = scale;
            this.id = id;
        }
    }
}

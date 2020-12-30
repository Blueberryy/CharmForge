package svenhjol.charm.container;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.helper.ItemNBTHelper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class AtlasInventory implements INamedContainerProvider, IInventory {
    public static final String CONTENTS = "contents";
    public static final String ACTIVE_MAP = "active_map";
    public static final String SCALE = "scale";
    private static final int EMPTY_MAP_SLOTS = 3;
    private static final int FILLED_MAP_SLOTS = 15;

    private final NonNullList<ItemStack> items;
    private final World world;
    private final ItemStack itemStack;
    private final ITextComponent name;
    private final int scale;
    private final int radius;
    private final int diameter;
    private Table<Integer, Integer, MapInfo> mapInfos;
    private MapInfo activeMap = null;
    private boolean isOpen = false;

    public AtlasInventory(World world, ItemStack itemStack) {
        this(world, itemStack, Collections.emptyList());
        updateMapInfos();
    }

    public AtlasInventory(World world, ItemStack itemStack, List<MapInfo> mapInfos) {
        this.world = world;
        this.itemStack = itemStack;
        this.name = itemStack.getDisplayName();
        this.items = getInventory(itemStack);
        this.scale = ItemNBTHelper.getInt(itemStack, SCALE, 0);
        this.radius = 64 * (1 << scale);
        this.diameter = radius * 2;
        this.mapInfos = TreeBasedTable.create();
        mapInfos.forEach(this::putMapInfo);
    }

    private static NonNullList<ItemStack> getInventory(ItemStack itemStack) {
        CompoundNBT nbt = ItemNBTHelper.getCompound(itemStack, CONTENTS);
        NonNullList<ItemStack> items = NonNullList.withSize(EMPTY_MAP_SLOTS + FILLED_MAP_SLOTS, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, items);
        return items;
    }

    public static AtlasInventory readFrom(World world, PacketBuffer buffer) {
        ItemStack itemStack = buffer.readItemStack();
        NonNullList<MapInfo> mapInfos = NonNullList.create();
        for (int i = buffer.readInt(); i > 0; --i) {
            mapInfos.add(MapInfo.readFrom(buffer));
        }
        return new AtlasInventory(world, itemStack, mapInfos);
    }

    private void putMapInfo(MapInfo mapInfo) {
        mapInfos.put(convertCoordinateToIndex(mapInfo.x), convertCoordinateToIndex(mapInfo.z), mapInfo);
    }

    public int convertCoordinateToIndex(int coordinate) {
        return Math.floorDiv(coordinate, diameter);
    }

    private boolean isOnMap(MapInfo info, int x, int z) {
        return x >= info.x - radius && x < info.x + radius && z >= info.z - radius && z < info.z + radius;
    }

    private void updateMapInfos() {
        if (!world.isRemote) {
            mapInfos.clear();
            items.stream()
                    .filter(stack -> stack.getItem() == Items.FILLED_MAP)
                    .map(this::getMapInfo)
                    .filter(Objects::nonNull)
                    .forEach(this::putMapInfo);
            activeMap = null;
        }
    }

    private MapInfo getMapInfo(ItemStack itemStack) {
        MapData mapData = FilledMapItem.getMapData(itemStack, world);
        return mapData != null ? new MapInfo(mapData.xCenter, mapData.zCenter, FilledMapItem.getMapId(itemStack), items.indexOf(itemStack)) : null;
    }

    @Nullable
    public MapInfo updateActiveMap(ServerPlayerEntity player) {
        int x = convertCoordinateToIndex((int) player.getPosX() + 64);
        int z = convertCoordinateToIndex((int) player.getPosZ() + 64);
        MapInfo activeMap = mapInfos.get(x, z);
        if (activeMap == null) {
            activeMap = makeNewMap(player, (int) player.getPosX(), (int) player.getPosZ());
        }
        ItemNBTHelper.setInt(itemStack, ACTIVE_MAP, activeMap != null ? activeMap.id : -1);
        return activeMap;
    }

    private MapInfo makeNewMap(ServerPlayerEntity player, int x, int z) {
        if (!isOpen) {
            int from = -1;
            int to = -1;
            for (int i = 0; i < EMPTY_MAP_SLOTS; ++i) {
                ItemStack stack = getStackInSlot(i);
                if (stack.getItem() == Items.MAP) {
                    from = i;
                    break;
                }
            }
            for (int i = EMPTY_MAP_SLOTS; i < getSizeInventory(); ++i) {
                ItemStack stack = getStackInSlot(i);
                if (stack.isEmpty()) {
                    to = i;
                    break;
                }
            }
            if (from != -1 && to != -1) {
                if (!player.isCreative()) {
                    decrStackSize(from, 1);
                }
                ItemStack map = FilledMapItem.setupNewMap(world, x, z, (byte) scale, true, true);
                setInventorySlotContents(to, map);
                MapInfo mapInfo = getMapInfo(map);
                putMapInfo(mapInfo);
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 0.5f,
                        player.world.rand.nextFloat() * 0.1F + 0.9F);
                return mapInfo;
            }
        }
        return null;
    }

    @Nullable
    public MapData getActiveMap(ClientPlayerEntity player) {
        int activeId = ItemNBTHelper.getInt(itemStack, ACTIVE_MAP, -1);
        if (activeId == -1) return null;
        return player.world.getMapData(FilledMapItem.getMapName(activeId));
    }

    @Nullable
    public ItemStack getLastActiveMapItem() {
        int activeId = ItemNBTHelper.getInt(itemStack, ACTIVE_MAP, -1);
        if (activeId == -1) return null;
        return items.stream().filter(it -> FilledMapItem.getMapId(it) == activeId).findAny().orElse(null);
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
        return EMPTY_MAP_SLOTS + FILLED_MAP_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
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

    @Override
    public void openInventory(PlayerEntity player) {
        isOpen = true;
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), CharmSounds.BOOKSHELF_OPEN, SoundCategory.BLOCKS, 0.5f,
                player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        isOpen = false;
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), CharmSounds.BOOKSHELF_CLOSE, SoundCategory.BLOCKS, 0.5f,
                player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    public boolean hasItemStack(ItemStack stack) {
        return items.stream().anyMatch(it -> !it.isEmpty() && it.isItemEqual(stack));
    }

    public ItemStack getAtlasItem() {
        return itemStack;
    }

    public Table<Integer, Integer, MapInfo> getMapInfos() {
        return mapInfos;
    }

    public void writeTo(PacketBuffer buffer) {
        buffer.writeItemStack(itemStack);
        buffer.writeInt(mapInfos.size());
        mapInfos.values().forEach(it -> it.writeTo(buffer));
    }

    public static class MapInfo {
        public final int x;
        public final int z;
        public final int id;
        public final int slot;

        private MapInfo(int x, int z, int id, int slot) {
            this.x = x;
            this.z = z;
            this.id = id;
            this.slot = slot;
        }

        public static MapInfo readFrom(PacketBuffer buffer) {
            return new MapInfo(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
        }

        public void writeTo(PacketBuffer buffer) {
            buffer.writeInt(x);
            buffer.writeInt(z);
            buffer.writeInt(id);
            buffer.writeInt(slot);
        }
    }
}

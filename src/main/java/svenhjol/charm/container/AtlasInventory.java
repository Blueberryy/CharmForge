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
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.module.Atlas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class AtlasInventory implements INamedContainerProvider, IInventory {
    public static final String EMPTY_MAPS = "empty_maps";
    public static final String FILLED_MAPS = "filled_maps";
    public static final String ACTIVE_MAP = "active_map";
    public static final String SCALE = "scale";
    public static final String ID = "id";
    private static final int EMPTY_MAP_SLOTS = 3;
    private final World world;
    private int diameter;
    private final Map<Index, MapInfo> mapInfos;
    private final NonNullList<ItemStack> emptyMaps;
    private ItemStack atlas;
    private int scale;
    private boolean isOpen = false;

    public AtlasInventory(World world, ItemStack atlas) {
        this.world = world;
        this.atlas = atlas;
        this.scale = 0;
        this.diameter = 128;
        this.emptyMaps = NonNullList.withSize(EMPTY_MAP_SLOTS, ItemStack.EMPTY);
        this.mapInfos = new LinkedHashMap<>();
        load();
    }

    public void reload(ItemStack atlas) {
        this.atlas = atlas;
        emptyMaps.clear();
        mapInfos.clear();
        load();
    }

    private void load() {
        scale = ItemNBTHelper.getInt(atlas, SCALE, 0);
        diameter = 128 * (1 << scale);
        ItemStackHelper.loadAllItems(ItemNBTHelper.getCompound(atlas, EMPTY_MAPS), emptyMaps);
        ListNBT listNBT = ItemNBTHelper.getList(atlas, FILLED_MAPS);
        for (int i = 0; i < listNBT.size(); ++i) {
            putMapInfo(MapInfo.readFrom(listNBT.getCompound(i)));
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    private void putMapInfo(MapInfo mapInfo) {
        mapInfos.put(Index.of(convertCoordToIndex(mapInfo.x), convertCoordToIndex(mapInfo.z)), mapInfo);
    }

    public int convertCoordToIndex(int coordinate) {
        return Math.floorDiv(coordinate, diameter);
    }

    private MapInfo getMapInfo(ItemStack map) {
        MapData mapData = FilledMapItem.getMapData(map, world);
        return mapData != null ? new MapInfo(mapData.xCenter, mapData.zCenter, FilledMapItem.getMapId(map), map) : null;
    }

    public boolean updateActiveMap(ServerPlayerEntity player) {
        int x = convertCoordToIndex((int) player.getPosX() + 64);
        int z = convertCoordToIndex((int) player.getPosZ() + 64);
        MapInfo activeMap = mapInfos.get(Index.of(x, z));
        boolean madeNewMap = false;
        if (activeMap == null) {
            activeMap = makeNewMap(player, (int) player.getPosX(), (int) player.getPosZ());
            madeNewMap = activeMap != null;
        }
        if(activeMap != null) {
            Atlas.sendMapToClient(player, activeMap.map);
            ItemNBTHelper.setInt(atlas, ACTIVE_MAP, activeMap.id);
        } else {
            ItemNBTHelper.setInt(atlas, ACTIVE_MAP, -1);
        }
        return madeNewMap;
    }

    private MapInfo makeNewMap(ServerPlayerEntity player, int x, int z) {
        if (!isOpen) {
            for (int i = 0; i < EMPTY_MAP_SLOTS; ++i) {
                ItemStack stack = getStackInSlot(i);
                if (stack.getItem() == Items.MAP) {
                    if (!player.isCreative()) {
                        decrStackSize(i, 1);
                    }
                    ItemStack map = FilledMapItem.setupNewMap(world, x, z, (byte) scale, true, true);
                    MapInfo mapInfo = getMapInfo(map);
                    putMapInfo(mapInfo);
                    markDirty();
                    world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 0.5f,
                            player.world.rand.nextFloat() * 0.1F + 0.9F);
                    return mapInfo;
                }
            }
        }
        return null;
    }

    @Nullable
    public MapData getActiveMap() {
        int activeId = ItemNBTHelper.getInt(atlas, ACTIVE_MAP, -1);
        if (activeId == -1) return null;
        return world.getMapData(FilledMapItem.getMapName(activeId));
    }

    @Nullable
    public ItemStack getLastActiveMapItem() {
        int activeId = ItemNBTHelper.getInt(atlas, ACTIVE_MAP, -1);
        if (activeId == -1) return null;
        return mapInfos.values().stream().filter(it -> it.id == activeId).findAny().map(it -> it.map).orElse(null);
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return atlas.getDisplayName();
    }

    @Nullable
    @Override
    public Container createMenu(int syncId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new AtlasContainer(syncId, playerInventory, this);
    }

    @Override
    public int getSizeInventory() {
        return EMPTY_MAP_SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.emptyMaps) {
            if (!stack.isEmpty()) return false;
        }
        return mapInfos.isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return emptyMaps.get(index);
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(emptyMaps, index, count);
        markDirty();
        return itemstack;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack itemStack = ItemStackHelper.getAndRemove(emptyMaps, index);
        markDirty();
        return itemStack;
    }

    public MapInfo removeMapByCoords(int x, int z) {
        return removeMapByIndex(Index.of(convertCoordToIndex(x), convertCoordToIndex(z)));
    }

    private MapInfo removeMapByIndex(Index index) {
        MapInfo info = mapInfos.remove(index);
        markDirty();
        return info;
    }

    public void addToInventory(ItemStack itemStack) {
        if (itemStack.getItem() == Items.FILLED_MAP) {
            putMapInfo(getMapInfo(itemStack));
            markDirty();
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        if (!emptyMaps.get(index).equals(stack, true)) {
            emptyMaps.set(index, stack);
            if (stack.getCount() > getInventoryStackLimit()) {
                stack.setCount(getInventoryStackLimit());
            }
            markDirty();
        }
    }

    @Override
    public void markDirty() {
        ItemNBTHelper.setInt(atlas, SCALE, scale);
        CompoundNBT emptyMapNBT = new CompoundNBT();
        ItemStackHelper.saveAllItems(emptyMapNBT, emptyMaps, false);
        ItemNBTHelper.setCompound(atlas, EMPTY_MAPS, emptyMapNBT);
        ListNBT listNBT = new ListNBT();
        for (MapInfo mapInfo : mapInfos.values()) {
            CompoundNBT nbt = new CompoundNBT();
            mapInfo.writeTo(nbt);
            listNBT.add(nbt);
        }
        ItemNBTHelper.setList(atlas, FILLED_MAPS, listNBT);
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
        for (Hand hand : Hand.values()) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (heldItem.getItem() == Atlas.ATLAS_ITEM && Objects.equals(ItemNBTHelper.getUuid(atlas, ID), ItemNBTHelper.getUuid(heldItem, ID))) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        emptyMaps.clear();
        mapInfos.clear();
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
        return Stream.concat(emptyMaps.stream(), mapInfos.values().stream().map(it -> it.map)).anyMatch(it -> !it.isEmpty() && it.isItemEqual(stack));
    }

    public ItemStack getAtlasItem() {
        return atlas;
    }

    public Map<Index, MapInfo> getMapInfos() {
        return mapInfos;
    }

    public int getScale() {
        return scale;
    }

    public static class MapInfo {
        private static final String X = "x";
        private static final String Z = "z";
        private static final String ID = "id";
        private static final String MAP = "map";
        public final int x;
        public final int z;
        public final int id;
        public final ItemStack map;

        private MapInfo(int x, int z, int id, ItemStack map) {
            this.x = x;
            this.z = z;
            this.id = id;
            this.map = map;
        }

        public static MapInfo readFrom(CompoundNBT nbt) {
            return new MapInfo(nbt.getInt(X), nbt.getInt(Z), nbt.getInt(ID), ItemStack.read(nbt.getCompound(MAP)));
        }

        public void writeTo(CompoundNBT nbt) {
            nbt.putInt(X, x);
            nbt.putInt(Z, z);
            nbt.putInt(ID, id);
            CompoundNBT mapNBT = new CompoundNBT();
            map.write(mapNBT);
            nbt.put(MAP, mapNBT);
        }
    }

    public static class Index {
        private static final Table<Integer, Integer, Index> cache = TreeBasedTable.create();
        public final int x;
        public final int y;

        private Index(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Index index = (Index) o;
            return x == index.x && y == index.y;
        }

        @Override
        public int hashCode() {
            return 7079 * x + y;
        }

        public static Index of(int x, int y) {
            Index index = cache.get(x, y);
            if (index == null) {
                index = new Index(x, y);
                cache.put(x, y, index);
            }
            return index;
        }
    }
}

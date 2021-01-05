package svenhjol.charm.container;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.module.Atlas;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class AtlasInventory implements INamedContainerProvider, IInventory {
    public static final String EMPTY_MAPS = "empty_maps";
    public static final String FILLED_MAPS = "filled_maps";
    public static final String ACTIVE_MAP = "active_map";
    public static final String SCALE = "scale";
    public static final String ID = "id";
    private static final int EMPTY_MAP_SLOTS = 3;
    private final Table<RegistryKey<World>, Index, MapInfo> mapInfos;
    private final NonNullList<ItemStack> emptyMaps;
    private int diameter;
    private ItemStack atlas;
    private int scale;
    private boolean isOpen = false;

    public AtlasInventory(ItemStack atlas) {
        this.atlas = atlas;
        this.scale = 0;
        this.diameter = 128;
        this.emptyMaps = NonNullList.withSize(EMPTY_MAP_SLOTS, ItemStack.EMPTY);
        this.mapInfos = HashBasedTable.create();
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
        mapInfos.put(mapInfo.dimension, convertCoordsToIndex(mapInfo.x, mapInfo.z), mapInfo);
    }

    public Index getIndexOf(PlayerEntity player) {
        return convertCoordsToIndex((int) player.getPosX() + 64,(int) player.getPosZ() + 64);
    }

    public Index convertCoordsToIndex(int x, int y) {
        return Index.of(convertCoordToIndex(x), convertCoordToIndex(y));
    }

    public int convertCoordToIndex(int coordinate) {
        return Math.floorDiv(coordinate, diameter);
    }

    private static MapInfo createMapInfo(World world, ItemStack map) {
        MapData mapData = FilledMapItem.getMapData(map, world);
        return mapData != null ? new MapInfo(mapData.xCenter, mapData.zCenter, FilledMapItem.getMapId(map), map, mapData.dimension) : null;
    }

    public boolean updateActiveMap(ServerPlayerEntity player) {
        MapInfo activeMap = mapInfos.get(player.world.getDimensionKey(), getIndexOf(player));
        boolean madeNewMap = false;
        if (activeMap == null && !isOpen) {
            activeMap = makeNewMap(player, (int) player.getPosX(), (int) player.getPosZ());
            madeNewMap = activeMap != null;
        }
        if (activeMap != null) {
            Atlas.sendMapToClient(player, activeMap.map);
            ItemNBTHelper.setInt(atlas, ACTIVE_MAP, activeMap.id);
        } else {
            ItemNBTHelper.setInt(atlas, ACTIVE_MAP, -1);
        }
        return madeNewMap;
    }

    private MapInfo makeNewMap(ServerPlayerEntity player, int x, int z) {
        for (int i = 0; i < EMPTY_MAP_SLOTS; ++i) {
            ItemStack stack = emptyMaps.get(i);
            if (stack.getItem() == Items.MAP) {
                if (!player.isCreative()) {
                    decrStackSize(i, 1);
                }
                ItemStack map = FilledMapItem.setupNewMap(player.world, x, z, (byte) scale, true, true);
                MapInfo mapInfo = createMapInfo(player.world, map);
                putMapInfo(mapInfo);
                markDirty();
                player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.BLOCKS, 0.5f,
                        player.world.rand.nextFloat() * 0.1F + 0.9F);
                return mapInfo;
            }
        }
        return null;
    }

    @Nullable
    public MapData getActiveMap(World world) {
        int activeId = ItemNBTHelper.getInt(atlas, ACTIVE_MAP, -1);
        return activeId == -1 ? null : world.getMapData(FilledMapItem.getMapName(activeId));
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

    public MapInfo removeMapByCoords(World world, int x, int z) {
        MapInfo info = mapInfos.remove(world.getDimensionKey(), convertCoordsToIndex(x, z));
        markDirty();
        return info;
    }

    public void addToInventory(World world, ItemStack itemStack) {
        if (itemStack.getItem() == Items.FILLED_MAP) {
            putMapInfo(createMapInfo(world, itemStack));
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
        player.playSound(CharmSounds.BOOKSHELF_OPEN, SoundCategory.BLOCKS, 0.5f, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        isOpen = false;
        player.playSound(CharmSounds.BOOKSHELF_CLOSE, SoundCategory.BLOCKS, 0.5f, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    public boolean hasItemStack(ItemStack stack) {
        return Stream.concat(emptyMaps.stream(), mapInfos.values().stream().map(it -> it.map)).anyMatch(it -> !it.isEmpty() && it.isItemEqual(stack));
    }

    public ItemStack getAtlasItem() {
        return atlas;
    }

    public Table<RegistryKey<World>, Index, MapInfo> getMapInfos() {
        return mapInfos;
    }

    public Map<Index, MapInfo> getCurrentDimensionMapInfos(World world) {
        return mapInfos.row(world.getDimensionKey());
    }

    public int getScale() {
        return scale;
    }

    public static class MapInfo {
        private static final String X = "x";
        private static final String Z = "z";
        private static final String ID = "id";
        private static final String MAP = "map";
        private static final String DIMENSION = "dimension";
        public final int x;
        public final int z;
        public final int id;
        public final ItemStack map;
        public final RegistryKey<World> dimension;

        private MapInfo(int x, int z, int id, ItemStack map, RegistryKey<World> dimension) {
            this.x = x;
            this.z = z;
            this.id = id;
            this.map = map;
            this.dimension = dimension;
        }

        public static MapInfo readFrom(CompoundNBT nbt) {
            return new MapInfo(nbt.getInt(X), nbt.getInt(Z), nbt.getInt(ID), ItemStack.read(nbt.getCompound(MAP)),
                    DimensionType.decodeWorldKey(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt.get(DIMENSION))).result().orElse(World.OVERWORLD));
        }

        public void writeTo(CompoundNBT nbt) {
            nbt.putInt(X, x);
            nbt.putInt(Z, z);
            nbt.putInt(ID, id);
            CompoundNBT mapNBT = new CompoundNBT();
            map.write(mapNBT);
            nbt.put(MAP, mapNBT);
            ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, dimension.getLocation()).result().ifPresent(it -> nbt.put(DIMENSION, it));
        }
    }

    public static class Index {
        private static final Int2ObjectMap<Int2ObjectMap<Index>> cache = new Int2ObjectOpenHashMap<>();
        public final int x;
        public final int y;

        private Index(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Index plus(Index value) {
            return transform(it -> it.apply(this) + it.apply(value));
        }

        public Index plus(int value) {
            return transform(it -> it.apply(this) + value);
        }

        public Index minus(Index value) {
            return transform(it -> it.apply(this) - it.apply(value));
        }

        public Index minus(int value) {
            return transform(it -> it.apply(this) - value);
        }

        public Index multiply(int value) {
            return transform(it -> it.apply(this) * value);
        }

        public Index divide(int value) {
            return transform(it -> it.apply(this) / value);
        }

        public Index clamp(Index min, Index max) {
            return transform(it -> MathHelper.clamp(it.apply(this), it.apply(min), it.apply(max)));
        }

        private Index transform(Function<Function<Index, Integer>, Integer> transformer) {
            return Index.of(transformer.apply(it -> it.x), transformer.apply(it -> it.y));
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
            Int2ObjectMap<Index> columnCache = cache.get(x);
            if (columnCache == null) {
                columnCache = new Int2ObjectOpenHashMap<>();
                cache.put(x, columnCache);
            }
            Index index = columnCache.get(y);
            if (index == null) {
                index = new Index(x, y);
                columnCache.put(y, index);
            }
            return index;
        }
    }
}

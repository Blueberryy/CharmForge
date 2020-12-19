package svenhjol.charm.container;

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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.module.Atlas;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AtlasInventory implements INamedContainerProvider, IInventory {
    public static String CONTENTS = "contents";
    public static String ACTIVE_MAP = "active_map";
    public static int SIZE = 18;

    private final NonNullList<ItemStack> items;
    private final World world;
    private final ItemStack itemStack;
    private final ITextComponent name;
    private List<MapInfo> mapInfos = new ArrayList<>();
    private MapInfo activeMap = null;
    private boolean isOpen = false;

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

    private static boolean isOnMap(MapInfo info, int x, int z) {
        return x >= info.x - info.scale && x < info.x + info.scale && z >= info.z - info.scale && z < info.z + info.scale;
    }

    private void updateMapInfos() {
        if (!world.isRemote) {
            mapInfos = items.stream()
                    .filter(stack -> stack.getItem() == Items.FILLED_MAP)
                    .map(this::getMapInfo)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(info -> info.scale))
                    .collect(Collectors.toList());
            activeMap = null;
        }
    }

    private MapInfo getMapInfo(ItemStack itemStack) {
        MapData mapData = FilledMapItem.getMapData(itemStack, world);
        return mapData != null ? new MapInfo(mapData.xCenter, mapData.zCenter, 64 * (1 << mapData.scale), FilledMapItem.getMapId(itemStack),
                items.indexOf(itemStack)) : null;
    }

    @Nullable
    public MapInfo updateActiveMap(ServerPlayerEntity player) {
        int x = (int) Math.floor(player.getPosX());
        int z = (int) Math.floor(player.getPosZ());
        MapInfo activeMap = null;
        if (this.activeMap != null && isOnMap(this.activeMap, x, z)) {
            activeMap = this.activeMap;
        }
        if (activeMap == null) {
            activeMap = mapInfos.stream()
                    .filter(info -> isOnMap(info, x, z))
                    .findFirst().orElse(null);
        }
        if (activeMap == null) {
            activeMap = makeNewMap(player, x, z);
        }
        ItemNBTHelper.setInt(itemStack, ACTIVE_MAP, activeMap != null ? activeMap.id : -1);
        return activeMap;
    }

    private MapInfo makeNewMap(ServerPlayerEntity player, int x, int z) {
        if (!isOpen) {
            int from = -1;
            int to = -1;
            for (int i = 0; i < getSizeInventory() && (from == -1 || to == -1); ++i) {
                ItemStack stack = getStackInSlot(i);
                if (stack.isEmpty()) {
                    if (to == -1) {
                        to = i;
                    }
                } else if (stack.getItem() == Items.MAP) {
                    if (from == -1) {
                        from = i;
                    }
                    if (!player.isCreative() && to == -1 && stack.getCount() == 1) {
                        to = i;
                    }
                }
            }
            if (from != -1 && to != -1) {
                if (!player.isCreative()) {
                    decrStackSize(from, 1);
                }
                ItemStack map = FilledMapItem.setupNewMap(world, x, z, (byte) Atlas.mapSize, true, true);
                setInventorySlotContents(to, map);
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT,
                        SoundCategory.BLOCKS, 0.5f, player.world.rand.nextFloat() * 0.1F + 0.9F);
                return getMapInfo(map);
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

    public static class MapInfo {
        public final double x;
        public final double z;
        public final int scale;
        public final int id;
        public final int slot;

        private MapInfo(double x, double z, int scale, int id, int slot) {
            this.x = x;
            this.z = z;
            this.scale = scale;
            this.id = id;
            this.slot = slot;
        }
    }
}

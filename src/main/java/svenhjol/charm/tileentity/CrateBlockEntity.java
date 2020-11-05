package svenhjol.charm.TileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import svenhjol.charm.module.Crates;
import svenhjol.charm.screenhandler.CrateScreenHandler;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CrateTileEntity extends LockableLootTileEntity implements ISidedInventory {
    public static int SIZE = 9;
    private static final int[] SLOTS = IntStream.range(0, SIZE).toArray();
    private NonNullList<ItemStack> items = NonNullList.ofSize(SIZE, ItemStack.EMPTY);

    public CrateTileEntity() {
        super(Crates.BLOCK_ENTITY);
    }

    @Override
    public void fromTag(BlockState state, CompoundNBT tag) {
        super.fromTag(state, tag);
        this.items = NonNullList.ofSize(SIZE, ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag))
            Inventories.fromTag(tag, this.items);
    }

    @Override
    public CompoundNBT toTag(CompoundNBT tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag))
            Inventories.toTag(tag, this.items, false);

        return tag;
    }

    @Override
    public NonNullList<ItemStack> getInvStackList() {
        return this.items;
    }

    @Override
    protected void setInvStackList(NonNullList<ItemStack> list) {
        this.items = list;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return Crates.canCrateInsertItem(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
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

    @Nullable
    @Override
    public Text getCustomName() {
        return new TranslatableText("container.charm.crate");
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.charm.crate");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new CrateScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5F, player.world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onClose(PlayerEntity player) {
        player.world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5F, player.world.random.nextFloat() * 0.1F + 0.9F);
    }
}

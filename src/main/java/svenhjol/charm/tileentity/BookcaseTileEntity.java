package svenhjol.charm.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.block.BookcaseBlock;
import svenhjol.charm.module.Bookcases;
import svenhjol.charm.container.BookcaseContainer;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class BookcaseTileEntity extends LockableLootTileEntity implements ISidedInventory {
    public static int SIZE = 18;
    private static final int[] SLOTS = IntStream.range(0, SIZE).toArray();
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public BookcaseTileEntity() {
        super(Bookcases.BLOCK_ENTITY);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        if (!this.checkLootAndRead(tag))
            ItemStackHelper.loadAllItems(tag, this.items);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        if (!this.checkLootAndWrite(tag))
            ItemStackHelper.saveAllItems(tag, this.items);

        return tag;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> list) {
        this.items = list;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("container.charm.bookcase");
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.charm.bookcase");
    }

    @Override
    protected Container createMenu(int syncId, PlayerInventory playerInventory) {
        return new BookcaseContainer(syncId, playerInventory, this);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        updateBlockState();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, @Nullable Direction dir) {
        return Bookcases.canContainItem(stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return SIZE;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        player.world.playSound(null, pos, CharmSounds.BOOKSHELF_OPEN, SoundCategory.BLOCKS, 0.5f, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        player.world.playSound(null, pos, CharmSounds.BOOKSHELF_CLOSE, SoundCategory.BLOCKS, 0.5f, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void markDirty() {
        updateBlockState();
        super.markDirty();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    protected void updateBlockState() {
        int filled = 0;

        for (int i = 0; i < SIZE; i++) {
            if (world == null)
                continue;

            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty())
                filled++;
        }

        if (world != null && world.getBlockState(pos).getBlock() instanceof BookcaseBlock)
            world.setBlockState(pos, world.getBlockState(pos).with(BookcaseBlock.SLOTS, filled), 2);
    }
}

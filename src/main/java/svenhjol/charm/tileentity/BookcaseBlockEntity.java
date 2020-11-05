package svenhjol.charm.TileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.NonNullList;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.block.BookcaseBlock;
import svenhjol.charm.module.Bookcases;
import svenhjol.charm.screenhandler.BookcaseScreenHandler;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class BookcaseTileEntity extends LockableLootTileEntity implements ISidedInventory {
    public static int SIZE = 18;
    private static final int[] SLOTS = IntStream.range(0, SIZE).toArray();
    private NonNullList<ItemStack> items = NonNullList.ofSize(SIZE, ItemStack.EMPTY);

    public BookcaseTileEntity() {
        super(Bookcases.BLOCK_ENTITY);
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
            Inventories.toTag(tag, this.items);

        return tag;
    }

    @Override
    protected NonNullList<ItemStack> getInvStackList() {
        return this.items;
    }

    @Override
    protected void setInvStackList(NonNullList<ItemStack> list) {
        this.items = list;
    }

    @Nullable
    @Override
    public Text getCustomName() {
        return new TranslatableText("container.charm.bookcase");
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.charm.bookcase");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new BookcaseScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        updateBlockState();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return Bookcases.canContainItem(stack);
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
    public void onOpen(PlayerEntity player) {
        player.world.playSound(null, pos, CharmSounds.BOOKSHELF_OPEN, SoundCategory.BLOCKS, 0.5f, player.world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onClose(PlayerEntity player) {
        player.world.playSound(null, pos, CharmSounds.BOOKSHELF_CLOSE, SoundCategory.BLOCKS, 0.5f, player.world.random.nextFloat() * 0.1F + 0.9F);
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

            ItemStack stack = getStack(i);
            if (stack == null || !stack.isEmpty())
                filled++;
        }

        if (world != null && world.getBlockState(pos).getBlock() instanceof BookcaseBlock)
            world.setBlockState(pos, world.getBlockState(pos).with(BookcaseBlock.SLOTS, filled), 2);
    }
}

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
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import svenhjol.charm.module.Crates;
import svenhjol.charm.container.CrateContainer;
import vazkii.quark.api.ITransferManager;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CrateTileEntity extends LockableLootTileEntity implements ICapabilityProvider, ISidedInventory, ITransferManager {
    public static int SIZE = 9;
    private static final int[] SLOTS = IntStream.range(0, SIZE).toArray();
    private NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);

    public CrateTileEntity() {
        super(Crates.TILE_ENTITY);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        this.items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        if (!this.checkLootAndRead(tag))
            ItemStackHelper.loadAllItems(tag, this.items);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        if (!this.checkLootAndWrite(tag))
            ItemStackHelper.saveAllItems(tag, this.items, false);

        return tag;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> list) {
        this.items = list;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, @Nullable Direction dir) {
        return Crates.canCrateInsertItem(stack);
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
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty())
                return false;
        }
        return true;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return new TranslationTextComponent("container.charm.crate");
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.charm.crate");
    }

    @Override
    protected Container createMenu(int syncId, PlayerInventory playerInventory) {
        return new CrateContainer(syncId, playerInventory, this);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_BARREL_OPEN, SoundCategory.BLOCKS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_BARREL_CLOSE, SoundCategory.BLOCKS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean acceptsTransfer(PlayerEntity playerEntity) {
        return true;
    }
}

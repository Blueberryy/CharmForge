package svenhjol.charm.container;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.module.Woodcutters;
import svenhjol.charm.recipe.WoodcuttingRecipe;

import java.util.List;

public class WoodcutterContainer extends Container {
    private final IWorldPosCallable context;
    private final IntReferenceHolder selectedRecipe;
    private final World world;
    private List<WoodcuttingRecipe> recipes;
    private ItemStack inputStack;
    private long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    private Runnable contentsChangedListener;
    public final Inventory inputInventory;
    private final CraftResultInventory inventory;

    public WoodcutterContainer(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, IWorldPosCallable.DUMMY);
    }

    public WoodcutterContainer(int syncId, PlayerInventory playerInventory, final IWorldPosCallable context) {
        super(Woodcutters.SCREEN_HANDLER, syncId);
        this.selectedRecipe = IntReferenceHolder.single();
        this.recipes = Lists.newArrayList();
        this.inputStack = ItemStack.EMPTY;
        this.contentsChangedListener = () -> {
        };
        this.inputInventory = new Inventory(1) {
            public void markDirty() {
                super.markDirty();
                WoodcutterContainer.this.onCraftMatrixChanged(this);
                WoodcutterContainer.this.contentsChangedListener.run();
            }
        };
        this.inventory = new CraftResultInventory();
        this.context = context;
        this.world = playerInventory.player.world;
        this.inputSlot = this.addSlot(new Slot(this.inputInventory, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.inventory, 1, 143, 33) {
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                stack.onCrafting(player.world, player, stack.getCount());
                WoodcutterContainer.this.inventory.onCrafting(player);
                ItemStack itemStack = WoodcutterContainer.this.inputSlot.decrStackSize(1);
                if (!itemStack.isEmpty()) {
                    WoodcutterContainer.this.updateRecipeResultSlot();
                }

                context.consume((world, blockPos) -> {
                    long l = world.getGameTime();
                    if (WoodcutterContainer.this.lastTakeTime != l) {
                        world.playSound(null, blockPos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        WoodcutterContainer.this.lastTakeTime = l;
                    }

                });
                return super.onTake(player, stack);
            }
        });

        int k;
        for(k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }

        for(k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.trackInt(this.selectedRecipe);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    @OnlyIn(Dist.CLIENT)
    public List<WoodcuttingRecipe> getRecipeList() {
        return this.recipes;
    }

    @OnlyIn(Dist.CLIENT)
    public int getRecipeListSize() {
        return this.recipes.size();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasItemsInInputSlot() {
        return this.inputSlot.getHasStack() && !this.recipes.isEmpty();
    }

    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(this.context, player, Woodcutters.WOODCUTTER);
    }

    @Override
    public boolean enchantItem(PlayerEntity player, int id) {
        if (this.func_241818_d_(id)) {
            this.selectedRecipe.set(id);
            this.updateRecipeResultSlot();
        }

        return true;
    }

    private boolean func_241818_d_(int i) {
        return i >= 0 && i < this.recipes.size();
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        if (itemStack.getItem() != this.inputStack.getItem()) {
            this.inputStack = itemStack.copy();
            this.updateAvailableRecipes(inventory, itemStack);
        }

    }

    private void updateAvailableRecipes(IInventory input, ItemStack stack) {
        this.recipes.clear();
        this.selectedRecipe.set(-1);
        this.outputSlot.putStack(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            this.recipes = this.world.getRecipeManager().getRecipes(Woodcutters.RECIPE_TYPE, input, this.world);
        }

    }

    private void updateRecipeResultSlot() {
        if (!this.recipes.isEmpty() && this.func_241818_d_(this.selectedRecipe.get())) {
            WoodcuttingRecipe woodcuttingRecipe = this.recipes.get(this.selectedRecipe.get());
            this.inventory.setRecipeUsed(woodcuttingRecipe);
            this.outputSlot.putStack(woodcuttingRecipe.getCraftingResult(this.inputInventory));
        } else {
            this.outputSlot.putStack(ItemStack.EMPTY);
        }

        this.detectAndSendChanges();
    }

    public ContainerType<?> getType() {
        return Woodcutters.SCREEN_HANDLER;
    }

    @OnlyIn(Dist.CLIENT)
    public void setInventoryUpdateListener(Runnable runnable) {
        this.contentsChangedListener = runnable;
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.inventory && super.canMergeSlot(stack, slot);
    }

    // copypasta from StonecutterContainer
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            Item item = itemstack1.getItem();
            itemstack = itemstack1.copy();
            if (index == 1) {
                item.onCreated(itemstack1, playerIn.world, playerIn);
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.world.getRecipeManager().getRecipe(IRecipeType.STONECUTTING, new Inventory(itemstack1), this.world).isPresent()) {
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }

            slot.onSlotChanged();
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
            this.detectAndSendChanges();
        }

        return itemstack;
    }

    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);
        this.inventory.removeStackFromSlot(1);
        this.context.consume((world, blockPos) -> {
            this.clearContainer(player, player.world, this.inputInventory);
        });
    }
}

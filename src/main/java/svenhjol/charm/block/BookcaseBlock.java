package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlockWithEntity;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.tileentity.BookcaseTileEntity;

import javax.annotation.Nullable;

public class BookcaseBlock extends CharmBlockWithEntity {
    public static final IntegerProperty SLOTS = IntegerProperty.create("slots", 0, BookcaseTileEntity.SIZE);

    protected CharmModule module;
    protected IVariantMaterial type;

    public BookcaseBlock(CharmModule module, IVariantMaterial type) {
        super(module, type.getString() + "_bookcase", AbstractBlock.Properties.from(Blocks.BOOKSHELF));

        this.module = module;
        this.type = type;

        setDefaultState(getDefaultState().with(SLOTS, 0));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote && !player.isSpectator()) {

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof BookcaseTileEntity) {
                BookcaseTileEntity bookcase = (BookcaseTileEntity)tileEntity;
                bookcase.fillWithLoot(player);
                player.openContainer(bookcase);
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof BookcaseTileEntity) {
                InventoryHelper.dropInventoryItems(world, pos, (IInventory)tile);
                world.updateComparatorOutputLevel(pos, this);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.hasDisplayName()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof BookcaseTileEntity)
                ((BookcaseTileEntity) tile).setCustomName(stack.getDisplayName());
        }
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        return state.get(SLOTS) > 0 ? 1 : 0;
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.DECORATIONS;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return state.get(SLOTS);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        BookcaseTileEntity bookcase = new BookcaseTileEntity();
        bookcase.setCustomName(new TranslationTextComponent("block." + module.mod + "." + type.getString() + "_bookcase"));
        return bookcase;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(SLOTS);
    }
}

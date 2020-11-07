package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlockWithEntity;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.module.Crates;
import svenhjol.charm.tileentity.CrateTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class CrateBlock extends CharmBlockWithEntity {
    private static final String BLOCK_ENTITY_TAG = "TileEntityTag";
    private static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    private IVariantMaterial type;

    public CrateBlock(CharmModule module, IVariantMaterial type) {
        super(module, type.getString() + "_crate", AbstractBlock.Properties
            .create(Material.WOOD)
            .sound(SoundType.WOOD)
            .hardnessAndResistance(1.5F));

        this.type = type;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        CrateTileEntity crate = new CrateTileEntity();
        crate.setCustomName(new TranslationTextComponent("block." + module.mod + "." + type.getString() + "_crate"));
        return crate;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.DECORATIONS;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof CrateTileEntity) {
                ((CrateTileEntity)tileEntity).setCustomName(itemStack.getDisplayName());
            }
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CrateTileEntity) {
            CrateTileEntity crate = (CrateTileEntity)tileEntity;

            if (!world.isRemote && player.isCreative() && !crate.isEmpty()) {
                ItemStack stack = new ItemStack(getBlockByMaterial(this.type));
                CompoundNBT tag = crate.write(new CompoundNBT());

                if (!tag.isEmpty())
                    stack.setTagInfo(BLOCK_ENTITY_TAG, tag);

                if (crate.hasCustomName())
                    stack.setDisplayName(crate.getCustomName());

                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                entity.setDefaultPickupDelay();
                world.addEntity(entity);
            } else {
                crate.fillWithLoot(player);
            }
        }

        super.onBlockHarvested(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity tileEntity = builder.get(LootParameters.BLOCK_ENTITY);
        if (tileEntity instanceof CrateTileEntity) {
            CrateTileEntity crate = (CrateTileEntity)tileEntity;

            builder = builder.withDynamicDrop(CONTENTS, ((context, consumer) -> {
                for (int i = 0; i < crate.getSizeInventory(); i++) {
                    consumer.accept(crate.getStackInSlot(i));
                }
            }));
        }
        return super.getDrops(state, builder);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isRemote && !player.isSpectator()) {

            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof CrateTileEntity) {
                CrateTileEntity crate = (CrateTileEntity)tileEntity;
                crate.fillWithLoot(player);
                player.openContainer(crate);
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof CrateTileEntity)
                world.updateComparatorOutputLevel(pos, state.getBlock());

            super.onReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
        return Container.calcRedstoneFromInventory((IInventory)world.getTileEntity(pos));
    }

    @Override
    public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getItem(world, pos, state);
        CrateTileEntity crate = (CrateTileEntity)world.getTileEntity(pos);

        if (crate == null)
            return ItemStack.EMPTY;

        CompoundNBT tag = crate.write(new CompoundNBT());
        if (!tag.isEmpty())
            stack.setTagInfo(BLOCK_ENTITY_TAG, tag);

        return stack;
    }

    private static Block getBlockByMaterial(IVariantMaterial type) {
        return Crates.CRATE_BLOCKS.get(type);
    }
}

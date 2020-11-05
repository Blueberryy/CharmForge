package svenhjol.charm.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.TileEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import svenhjol.charm.TileEntity.CrateTileEntity;
import svenhjol.charm.module.Crates;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.CharmBlockWithEntity;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;
import java.util.List;

public class CrateBlock extends CharmBlockWithEntity {
    private static final String BLOCK_ENTITY_TAG = "TileEntityTag";
    private static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    private IVariantMaterial type;

    public CrateBlock(CharmModule module, IVariantMaterial type) {
        super(module, type.asString() + "_crate", AbstractBlock.Settings
            .of(Material.WOOD)
            .sounds(BlockSoundGroup.WOOD)
            .strength(1.5F));

        this.type = type;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockView world) {
        CrateTileEntity crate = new CrateTileEntity();
        crate.setCustomName(new TranslatableText("block." + module.mod + "." + type.asString() + "_crate"));
        return crate;
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            TileEntity TileEntity = world.getTileEntity(pos);

            if (TileEntity instanceof CrateTileEntity) {
                ((CrateTileEntity)TileEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity TileEntity = world.getTileEntity(pos);
        if (TileEntity instanceof CrateTileEntity) {
            CrateTileEntity crate = (CrateTileEntity)TileEntity;

            if (!world.isRemote && player.isCreative() && !crate.isEmpty()) {
                ItemStack stack = new ItemStack(getBlockByMaterial(this.type));
                CompoundNBT tag = crate.toTag(new CompoundNBT());

                if (!tag.isEmpty())
                    stack.putSubTag(BLOCK_ENTITY_TAG, tag);

                if (crate.hasCustomName())
                    stack.setCustomName(crate.getCustomName());

                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            } else {
                crate.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        TileEntity TileEntity = builder.get(LootContextParameters.BLOCK_ENTITY);
        if (TileEntity instanceof CrateTileEntity) {
            CrateTileEntity crate = (CrateTileEntity)TileEntity;

            builder = builder.putDrop(CONTENTS, ((context, consumer) -> {
                for (int i = 0; i < crate.size(); i++) {
                    consumer.accept(crate.getStack(i));
                }
            }));
        }
        return super.getDroppedStacks(state, builder);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isRemote && !player.isSpectator()) {

            // original implementation with loot check
            TileEntity TileEntity = world.getTileEntity(pos);
            if (TileEntity instanceof CrateTileEntity) {
                CrateTileEntity crate = (CrateTileEntity)TileEntity;
                crate.checkLootInteraction(player);
                player.openHandledScreen(crate);
            }

            // fabric default implementation
            if (false) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity TileEntity = world.getTileEntity(pos);
            if (TileEntity instanceof CrateTileEntity)
                world.updateComparators(pos, state.getBlock());

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getTileEntity(pos));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        CrateTileEntity crate = (CrateTileEntity)world.getTileEntity(pos);

        if (crate == null)
            return ItemStack.EMPTY;

        CompoundNBT tag = crate.toTag(new CompoundNBT());
        if (!tag.isEmpty())
            stack.putSubTag(BLOCK_ENTITY_TAG, tag);

        return stack;
    }

    private static Block getBlockByMaterial(IVariantMaterial type) {
        return Crates.CRATE_BLOCKS.get(type);
    }
}

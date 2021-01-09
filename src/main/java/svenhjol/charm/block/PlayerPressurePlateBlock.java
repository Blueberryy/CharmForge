package svenhjol.charm.block;

import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.block.ICharmBlock;

import javax.annotation.Nonnull;
import java.util.List;

public class PlayerPressurePlateBlock extends AbstractPressurePlateBlock implements ICharmBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final CharmModule module;

    public PlayerPressurePlateBlock(CharmModule module) {
        super(Block.Properties.create(Material.ROCK, MaterialColor.BLACK)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .doesNotBlockMovement()
                .hardnessAndResistance(2F, 1200.0F));
        this.module = module;
        register(module, "player_pressure_plate");
        setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.REDSTONE;
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (enabled()) super.fillItemGroup(group, items);
    }

    @Override
    public boolean enabled() {
        return module.enabled;
    }

    @Override
    protected void playClickOnSound(@Nonnull IWorld worldIn, @Nonnull BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }

    @Override
    protected void playClickOffSound(@Nonnull IWorld worldIn, @Nonnull BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.4F);
    }

    @Override
    protected int computeRedstoneStrength(@Nonnull World worldIn, @Nonnull BlockPos pos) {
        AxisAlignedBB bounds = PRESSURE_AABB.offset(pos);
        List<? extends Entity> entities = worldIn.getEntitiesWithinAABB(PlayerEntity.class, bounds);
        return entities.stream().anyMatch(it -> !it.doesEntityNotTriggerPressurePlate()) ? 15 : 0;
    }

    @Override
    protected int getRedstoneStrength(@Nonnull BlockState state) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Nonnull
    @Override
    protected BlockState setRedstoneStrength(@Nonnull BlockState state, int strength) {
        return state.with(POWERED, strength > 0);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}

package svenhjol.charm.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import svenhjol.charm.block.GlowballBlobBlock;
import svenhjol.charm.module.Glowballs;

public class GlowballEntity extends ProjectileItemEntity {
    public GlowballEntity(EntityType<? extends GlowballEntity> entityType, World world) {
        super(entityType, world);
    }

    public GlowballEntity(World world, LivingEntity owner) {
        super(Glowballs.GLOWBALL, owner, world);
    }

    @OnlyIn(Dist.CLIENT)
    public GlowballEntity(World world, double x, double y, double z) {
        super(Glowballs.GLOWBALL, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Glowballs.GLOWBALL_ITEM;
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        super.onImpact(hitResult);
        this.remove();

        if (!world.isRemote) {
            if (hitResult.getType() == RayTraceResult.Type.BLOCK) {
                tryPlaceBlob(world, (BlockRayTraceResult)hitResult);
            } else if (hitResult.getType() == RayTraceResult.Type.ENTITY) {
                tryHitEntity((EntityRayTraceResult)hitResult);
            }
        }
    }

    private void tryPlaceBlob(World world, BlockRayTraceResult hitResult) {
        BlockPos pos = hitResult.getPos();
        Direction side = hitResult.getFace();
        BlockState state = world.getBlockState(pos);
        BlockPos offsetPos = pos.offset(side);

        if (state.isSolidSide(world, pos, side) && (world.isAirBlock(offsetPos) || world.hasWater(offsetPos))) {
            BlockState placedState = Glowballs.GLOWBALL_BLOCK.getDefaultState()
                .with(GlowballBlobBlock.FACING, side);

            BlockState offsetState = world.getBlockState(offsetPos);
            if (offsetState.getBlock() == Blocks.WATER)
                placedState = placedState.with(BlockStateProperties.WATERLOGGED, true);

            world.setBlockState(offsetPos, placedState, 2);
            world.playSound(null, offsetPos, SoundEvents.BLOCK_NYLIUM_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return;
        }

        if (this.func_234616_v_() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)this.func_234616_v_();

            if (!player.isCreative()) {
                world.playSound(null, pos, SoundEvents.BLOCK_NYLIUM_PLACE, SoundCategory.PLAYERS, 0.7F, 1.0F);
                world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GLOWSTONE_DUST, 1)));
            }
        }
    }

    private void tryHitEntity(EntityRayTraceResult hitResult) {
        Entity entity = hitResult.getEntity();
        entity.attackEntityFrom(DamageSource.causeThrownDamage(this, this.func_234616_v_()), 1);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

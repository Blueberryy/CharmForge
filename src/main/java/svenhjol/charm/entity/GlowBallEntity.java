package svenhjol.charm.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import svenhjol.charm.base.helper.PlayerHelper;
import svenhjol.charm.module.GlowBalls;
import svenhjol.charm.module.PlaceableGlowstoneDust;

public class GlowBallEntity extends ProjectileItemEntity {
    public GlowBallEntity(EntityType<? extends GlowBallEntity> entityType, World world) {
        super(entityType, world);
    }

    public GlowBallEntity(World world, LivingEntity owner) {
        super(GlowBalls.ENTITY, owner, world);
    }

    @OnlyIn(Dist.CLIENT)
    public GlowBallEntity(World world, double x, double y, double z) {
        super(GlowBalls.ENTITY, x, y, z, world);
    }

    @Override
    protected Item getDefaultItem() {
        return GlowBalls.GLOW_BALL;
    }

    @Override
    protected void onImpact(RayTraceResult hitResult) {
        super.onImpact(hitResult);
        this.remove();

        if (!world.isRemote) {
            boolean result = PlaceableGlowstoneDust.tryPlaceDust(world, hitResult);

            if (result)
                return;

            // cannot place, return the glow ball
            if (this.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)this.getEntity();

                if (!player.isCreative()) {
                    world.playSound(null, player.getPosition(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 0.7F, 1.0F);
                    PlayerHelper.addOrDropStack(player, new ItemStack(GlowBalls.GLOW_BALL));
                }
            }
        }
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

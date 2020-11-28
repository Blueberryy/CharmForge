package svenhjol.charm.entity;

import com.google.common.collect.Maps;
import net.minecraft.block.AbstractCoralPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.Charm;
import svenhjol.charm.module.CoralSquids;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

/**
 * Most of this is copypasta from SquidEntity.
 * canSpawn() checks for coral.
 */
public class CoralSquidEntity extends WaterMobEntity {
    public static final String CORAL_SQUID_TYPE_TAG = "CoralSquidType";

    private static final DataParameter<Integer> CORAL_SQUID_TYPE;
    public static final Map<Integer, ResourceLocation> TEXTURES;
    public static final Map<Integer, Item> DROPS;

    public float tiltAngle;
    public float prevTiltAngle;
    public float rollAngle;
    public float prevRollAngle;
    public float squidRotation;
    public float prevThrustTimer;
    public float tentacleAngle;
    public float prevTentacleAngle;
    private float swimVelocityScale;
    private float thrustTimerSpeed;
    private float turningSpeed;
    private float swimX;
    private float swimY;
    private float swimZ;

    public CoralSquidEntity(EntityType<? extends CoralSquidEntity> entityType, World world) {
        super(entityType, world);
        this.rand.setSeed(this.getEntityId());
        this.thrustTimerSpeed = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.28F;
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData entityData, @Nullable CompoundNBT entityTag) {
        entityData = super.onInitialSpawn(world, difficulty, spawnReason, entityData, entityTag);
        setCoralSquidType(rand.nextInt(5));
        return entityData;
    }

    public static boolean canSpawn(EntityType<CoralSquidEntity> type, IWorldReader world, SpawnReason spawnReason, BlockPos pos, Random random) {
        boolean coralBelow = false;

        for (int y = 0; y > -16; y--) {
            BlockPos downPos = pos.add(0, y, 0);
            BlockState downState = world.getBlockState(downPos);
            coralBelow = downState.getBlock() instanceof AbstractCoralPlantBlock;

            if (coralBelow)
                break;
        }

        boolean canSpawn = pos.getY() > 20
            && pos.getY() < world.getSeaLevel()
            && coralBelow;

        if (canSpawn)
            Charm.LOG.debug("Can spawn coral squid at " + pos.toString());

        return canSpawn;
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 4; // might be important for performance
    }

    public ResourceLocation getTexture() {
        return TEXTURES.getOrDefault(getCoralSquidType(), TEXTURES.get(0));
    }

    public int getCoralSquidType() {
        return this.dataManager.get(CORAL_SQUID_TYPE);
    }

    public void setCoralSquidType(int type) {
        if (type < 0 || type > 4)
            type = this.rand.nextInt(5);

        this.dataManager.set(CORAL_SQUID_TYPE, type);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(CORAL_SQUID_TYPE, 1);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putInt(CORAL_SQUID_TYPE_TAG, this.getCoralSquidType());
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.setCoralSquidType(tag.getInt(CORAL_SQUID_TYPE_TAG));
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new FleeGoal());
    }

    @Override
    protected void dropSpecialItems(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropSpecialItems(source, lootingMultiplier, allowDrops);
        Entity attacker = source.getTrueSource();

        if (attacker instanceof PlayerEntity && rand.nextFloat() < CoralSquids.dropChance)
            this.entityDropItem(DROPS.get(getCoralSquidType()));
    }

    public static AttributeModifierMap.MutableAttribute createSquidAttributes() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, 5.0D);
    }

    protected float getStandingEyeHeight(Pose pose, EntitySize dimensions) {
        return dimensions.height * 0.5F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_SQUID_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SQUID_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected float getSoundPitch() {
        return 1.F;
    }

    protected boolean canClimb() {
        return false;
    }

    public void livingTick() {
        super.livingTick();
        this.prevTiltAngle = this.tiltAngle;
        this.prevRollAngle = this.rollAngle;
        this.prevThrustTimer = this.squidRotation;
        this.prevTentacleAngle = this.tentacleAngle;
        this.squidRotation += this.thrustTimerSpeed;
        if ((double)this.squidRotation > 6.283185307179586D) {
            if (this.world.isRemote) {
                this.squidRotation = 6.2831855F;
            } else {
                this.squidRotation = (float)((double)this.squidRotation - 6.283185307179586D);
                if (this.rand.nextInt(7) == 0) {
                    this.thrustTimerSpeed = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
                }

                this.world.setEntityState(this, (byte)19);
            }
        }

        if (this.isInWaterOrBubbleColumn()) {
            if (this.squidRotation < 3.1415927F) {
                float f = this.squidRotation / 3.1415927F;
                this.tentacleAngle = MathHelper.sin(f * f * 3.1415927F) * 3.1415927F * 0.25F;
                if ((double)f > 0.75D) {
                    this.swimVelocityScale = 1.0F;
                    this.turningSpeed = 1.0F;
                } else {
                    this.turningSpeed *= 0.8F;
                }
            } else {
                this.tentacleAngle = 0.0F;
                this.swimVelocityScale *= 0.9F;
                this.turningSpeed *= 0.99F;
            }

            if (!this.world.isRemote) {
                this.setMotion((double)(this.swimX * this.swimVelocityScale), (double)(this.swimY * this.swimVelocityScale), (double)(this.swimZ * this.swimVelocityScale));
            }

            Vector3d vec3d = this.getMotion();
            float g = MathHelper.sqrt(horizontalMag(vec3d));
            this.renderYawOffset += (-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F - this.renderYawOffset) * 0.1F;
            this.rotationYaw = this.renderYawOffset;
            this.rollAngle = (float)((double)this.rollAngle + 3.141592653589793D * (double)this.turningSpeed * 1.5D);
            this.tiltAngle += (-((float)MathHelper.atan2((double)g, vec3d.y)) * 57.295776F - this.tiltAngle) * 0.1F;
        } else {
            this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.squidRotation)) * 3.1415927F * 0.25F;
            if (!this.world.isRemote) {
                double d = this.getMotion().y;
                if (this.isPotionActive(Effects.LEVITATION)) {
                    d = 0.05D * (double)(this.getActivePotionEffect(Effects.LEVITATION).getAmplifier() + 1);
                } else if (!this.hasNoGravity()) {
                    d -= 0.08D;
                }

                this.setMotion(0.0D, d * 0.981D, 0.0D);
            }

            this.tiltAngle = (float)((double)this.tiltAngle + (double)(-90.0F - this.tiltAngle) * 0.02D);
        }

    }

    public void travel(Vector3d movementInput) {
        this.move(MoverType.SELF, this.getMotion());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte status) {
        if (status == 19) {
            this.squidRotation = 0.0F;
        } else {
            super.handleStatusUpdate(status);
        }
    }

    /**
     * Sets the direction and velocity the squid must go when fleeing an enemy. Only has an effect when in the water.
     */
    public void setSwimmingVector(float x, float y, float z) {
        this.swimX = x;
        this.swimY = y;
        this.swimZ = z;
    }

    public boolean hasSwimmingVector() {
        return this.swimX != 0.0F || this.swimY != 0.0F || this.swimZ != 0.0F;
    }

    class FleeGoal extends Goal {
        private int timer;

        private FleeGoal() {
        }

        public boolean shouldExecute() {
            LivingEntity livingEntity = CoralSquidEntity.this.getRevengeTarget();
            if (CoralSquidEntity.this.isInWater() && livingEntity != null) {
                return CoralSquidEntity.this.getDistanceSq(livingEntity) < 100.0D;
            } else {
                return false;
            }
        }

        public void startExecuting() {
            this.timer = 0;
        }

        public void tick() {
            ++this.timer;
            LivingEntity livingEntity = CoralSquidEntity.this.getRevengeTarget();
            if (livingEntity != null) {
                Vector3d vec3d = new Vector3d(CoralSquidEntity.this.getPosX() - livingEntity.getPosX(), CoralSquidEntity.this.getPosY() - livingEntity.getPosY(), CoralSquidEntity.this.getPosZ() - livingEntity.getPosZ());
                BlockState blockState = CoralSquidEntity.this.world.getBlockState(new BlockPos(CoralSquidEntity.this.getPosX() + vec3d.x, CoralSquidEntity.this.getPosY() + vec3d.y, CoralSquidEntity.this.getPosZ() + vec3d.z));
                FluidState fluidState = CoralSquidEntity.this.world.getFluidState(new BlockPos(CoralSquidEntity.this.getPosX() + vec3d.x, CoralSquidEntity.this.getPosY() + vec3d.y, CoralSquidEntity.this.getPosZ() + vec3d.z));
                if (fluidState.isTagged(FluidTags.WATER) || blockState.isAir()) {
                    double d = vec3d.length();
                    if (d > 0.0D) {
                        vec3d.normalize();
                        float f = 3.0F;
                        if (d > 5.0D) {
                            f = (float)((double)f - (d - 5.0D) / 5.0D);
                        }

                        if (f > 0.0F) {
                            vec3d = vec3d.scale(f);
                        }
                    }

                    if (blockState.isAir()) {
                        vec3d = vec3d.subtract(0.0D, vec3d.y, 0.0D);
                    }

                    CoralSquidEntity.this.setSwimmingVector((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
                }

                if (this.timer % 10 == 5) {
                    CoralSquidEntity.this.world.addParticle(ParticleTypes.BUBBLE, CoralSquidEntity.this.getPosX(), CoralSquidEntity.this.getPosY(), CoralSquidEntity.this.getPosZ(), 0.0D, 0.0D, 0.0D);
                }

            }
        }
    }

    static class SwimGoal extends Goal {
        private final CoralSquidEntity squid;

        public SwimGoal(CoralSquidEntity squid) {
            this.squid = squid;
        }

        public boolean shouldExecute() {
            return true;
        }

        public void tick() {
            int i = this.squid.getIdleTime();
            if (i > 100) {
                this.squid.setSwimmingVector(0.0F, 0.0F, 0.0F);
            } else if (this.squid.getRNG().nextInt(50) == 0 || !this.squid.inWater || !this.squid.hasSwimmingVector()) {
                float f = this.squid.getRNG().nextFloat() * 6.2831855F;
                float g = MathHelper.cos(f) * 0.2F;
                float h = -0.1F + this.squid.getRNG().nextFloat() * 0.2F;
                float j = MathHelper.sin(f) * 0.2F;
                this.squid.setSwimmingVector(g, h, j);
            }
        }
    }

    static {
        CORAL_SQUID_TYPE = EntityDataManager.createKey(CoralSquidEntity.class, DataSerializers.VARINT);
        TEXTURES = Util.make(Maps.newHashMap(), map -> {
            map.put(0, new ResourceLocation(Charm.MOD_ID, "textures/entity/coral_squid/tube.png"));
            map.put(1, new ResourceLocation(Charm.MOD_ID, "textures/entity/coral_squid/brain.png"));
            map.put(2, new ResourceLocation(Charm.MOD_ID, "textures/entity/coral_squid/bubble.png"));
            map.put(3, new ResourceLocation(Charm.MOD_ID, "textures/entity/coral_squid/fire.png"));
            map.put(4, new ResourceLocation(Charm.MOD_ID, "textures/entity/coral_squid/horn.png"));
        });
        DROPS = Util.make(Maps.newHashMap(), map -> {
            map.put(0, Items.TUBE_CORAL);
            map.put(1, Items.BRAIN_CORAL);
            map.put(2, Items.BUBBLE_CORAL);
            map.put(3, Items.FIRE_CORAL);
            map.put(4, Items.HORN_CORAL);
        });
    }
}

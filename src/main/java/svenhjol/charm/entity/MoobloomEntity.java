package svenhjol.charm.entity;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;
import svenhjol.charm.Charm;
import svenhjol.charm.entity.goal.MoobloomPlantFlowerGoal;
import svenhjol.charm.module.Mooblooms;

import javax.annotation.Nullable;
import java.util.*;

public class MoobloomEntity extends CowEntity implements IShearable {
    private static final String TYPE_TAG = "Type";
    private static final String POLLINATED_TAG = "Pollinated";

    private static final DataParameter<String> TYPE;
    private static final DataParameter<Boolean> POLLINATED;
    public static Map<Type, ResourceLocation> TEXTURES = new HashMap<>();

    public MoobloomEntity(EntityType<? extends CowEntity> entityType, World world) {
        super(entityType, world);

        // set up the textures for each moobloom type
        TEXTURES = Util.make(Maps.newHashMap(), map -> {
            for (Type type : Type.values()) {
                map.put(type, new ResourceLocation(Charm.MOD_ID, "textures/entity/moobloom/" + type.name + ".png"));
            }
        });
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData entityData, @Nullable CompoundNBT entityTag) {
        entityData = super.onInitialSpawn(world, difficulty, spawnReason, entityData, entityTag);

        List<Type> types = Arrays.asList(Type.values());
        Type type = types.get(rand.nextInt(types.size()));
        setMoobloomType(type);

        return entityData;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TYPE, Type.ALLIUM.name());
        this.dataManager.register(POLLINATED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new MoobloomPlantFlowerGoal(this));
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ItemStack held = player.getHeldItem(hand);

        if (held.getItem() == Items.BOWL && !isChild()) {
            if (!world.isRemote && isPollinated()) {
                ItemStack stew;

                Optional<Pair<Effect, Integer>> optionalFlower = getEffectFromFlower(this.getMoobloomType().flower);

                if (optionalFlower.isPresent()) {
                    Pair<Effect, Integer> effectFromFlower = optionalFlower.get();

                    Effect effect = effectFromFlower.getLeft();
                    int duration = effectFromFlower.getRight() * 2;

                    stew = new ItemStack(Items.SUSPICIOUS_STEW);
                    playSound(SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK, 1.0F, 1.0F);
                    SuspiciousStewItem.addEffect(stew, effect, duration);
                } else {
                    stew = new ItemStack(Items.MUSHROOM_STEW);
                    playSound(SoundEvents.ENTITY_MOOSHROOM_MILK, 1.0F, 1.0F);
                }

                player.setHeldItem(hand, stew);
                this.dataManager.set(POLLINATED, false);
            }

            return ActionResultType.func_233537_a_(world.isRemote);

        } else if (held.getItem() == Items.SHEARS && isShearable()) {

            this.shear(SoundCategory.PLAYERS);
            if (!world.isRemote)
                held.damageItem(1, player, playerEntity -> playerEntity.sendBreakAnimation(hand));

            return ActionResultType.func_233537_a_(world.isRemote);
        }

        return super.func_230254_b_(player, hand);
    }

    @Override
    public void writeAdditional(CompoundNBT tag) {
        super.writeAdditional(tag);
        tag.putString(TYPE_TAG, this.getMoobloomType().name);
        tag.putBoolean(POLLINATED_TAG, this.dataManager.get(POLLINATED));
    }

    @Override
    public void readAdditional(CompoundNBT tag) {
        super.readAdditional(tag);
        this.setMoobloomType(Type.fromName(tag.getString(TYPE_TAG)));

        if (tag.contains(POLLINATED_TAG))
            this.dataManager.set(POLLINATED, tag.getBoolean(POLLINATED_TAG));
    }

    @Override
    public MoobloomEntity func_241840_a(ServerWorld serverWorld, AgeableEntity passiveEntity) {
        MoobloomEntity entity = Mooblooms.MOOBLOOM.create(serverWorld);
        Type childType = serverWorld.rand.nextFloat() < 0.5F ? this.getMoobloomType() : ((MoobloomEntity)passiveEntity).getMoobloomType();
        entity.setMoobloomType(childType);
        return entity;
    }

    public void pollinate() {
        world.playSound(null, getPosition(), SoundEvents.ENTITY_BEE_POLLINATE, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        this.dataManager.set(POLLINATED, true);
    }

    public boolean isPollinated() {
        return this.dataManager.get(POLLINATED);
    }

    public Type getMoobloomType() {
        return Type.fromName(this.dataManager.get(TYPE));
    }

    public void setMoobloomType(Type type) {
        this.dataManager.set(TYPE, type.name);
    }

    public ResourceLocation getMoobloomTexture() {
        return TEXTURES.getOrDefault(this.getMoobloomType(), TEXTURES.get(Type.ALLIUM));
    }

    public Optional<Pair<Effect, Integer>> getEffectFromFlower(BlockState flower) {
        Block block = flower.getBlock();
        if (block instanceof FlowerBlock) {
            FlowerBlock flowerBlock = (FlowerBlock)block;
            return Optional.of(Pair.of(flowerBlock.getStewEffect(), flowerBlock.getStewEffectDuration()));
        }

        return Optional.empty();
    }

    public static boolean canSpawn(EntityType<MoobloomEntity> type, IWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getLightSubtracted(pos, 0) > 8;
    }

    // copypasta from MooshroomEntity
    @Override
    public void shear(SoundCategory shearedSoundCategory) {
        this.world.playMovingSound(null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        if (!this.world.isRemote) {
            ((ServerWorld)this.world).spawnParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosYHeight(0.5D), this.getPosZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            this.remove();
            CowEntity cowEntity = EntityType.COW.create(this.world);
            cowEntity.setLocationAndAngles(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
            cowEntity.setHealth(this.getHealth());
            cowEntity.renderYawOffset = this.renderYawOffset;
            if (this.hasCustomName()) {
                cowEntity.setCustomName(this.getCustomName());
                cowEntity.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isNoDespawnRequired()) {
                cowEntity.enablePersistence();
            }

            cowEntity.setInvulnerable(this.isInvulnerable());
            this.world.addEntity(cowEntity);

            for(int i = 0; i < 5; ++i) {
                this.world.addEntity(new ItemEntity(this.world, this.getPosX(), this.getPosYHeight(1.0D), this.getPosZ(), new ItemStack(this.getMoobloomType().flower.getBlock())));
            }
        }
    }

    @Override
    public boolean isShearable() {
        return isAlive() && !this.isChild();
    }

    public enum Type {
        ALLIUM("allium", Blocks.ALLIUM.getDefaultState()),
        AZURE_BLUET("azure_bluet", Blocks.AZURE_BLUET.getDefaultState()),
        BLUE_ORCHID("blue_orchid", Blocks.BLUE_ORCHID.getDefaultState()),
        CORNFLOWER("cornflower", Blocks.CORNFLOWER.getDefaultState()),
        DANDELION("dandelion", Blocks.DANDELION.getDefaultState()),
        LILY_OF_THE_VALLEY("lily_of_the_valley", Blocks.LILY_OF_THE_VALLEY.getDefaultState()),
        ORANGE_TULIP("orange_tulip", Blocks.ORANGE_TULIP.getDefaultState()),
        PINK_TULIP("pink_tulip", Blocks.PINK_TULIP.getDefaultState()),
        RED_TULIP("red_tulip", Blocks.RED_TULIP.getDefaultState()),
        WHITE_TULIP("white_tulip", Blocks.WHITE_TULIP.getDefaultState()),
        OXEYE_DAISY("oxeye_daisy", Blocks.OXEYE_DAISY.getDefaultState()),
        POPPY("poppy", Blocks.POPPY.getDefaultState());

        private final String name;
        private final BlockState flower;

        Type(String name, BlockState flower) {
            this.name = name;
            this.flower = flower;
        }

        public BlockState getFlower() {
            return this.flower;
        }

        private static Type fromName(String name) {
            Type[] values = values();
            for (Type value : values) {
                if (value.name.equals(name))
                    return value;
            }

            return ALLIUM;
        }
    }

    static {
        TYPE = EntityDataManager.createKey(MoobloomEntity.class, DataSerializers.STRING);
        POLLINATED = EntityDataManager.createKey(MoobloomEntity.class, DataSerializers.BOOLEAN);
    }
}

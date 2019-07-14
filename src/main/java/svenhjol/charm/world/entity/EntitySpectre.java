package svenhjol.charm.world.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.world.event.SpectreAttackEvent;
import svenhjol.charm.world.feature.Spectre;
import svenhjol.charm.world.feature.SpectreHaunting;
import svenhjol.charm.world.message.MessageSpectreDespawn;
import svenhjol.meson.Meson;
import svenhjol.meson.handler.NetworkHandler;
import svenhjol.meson.helper.EnchantmentHelper;

import javax.annotation.Nullable;

public class EntitySpectre extends EntityZombie
{
    public static final ResourceLocation LOOT_TABLE = new ResourceLocation(Charm.MOD_ID + ":entities/spectre");
    public float eyeHeight;

    public EntitySpectre(World world)
    {
        super(world);
        eyeHeight = 1.75f;
        isImmuneToFire = true;
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Spectre.movementSpeed);
        getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Spectre.attackDamage);
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Spectre.maxHealth);
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(Spectre.trackingRange);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity)
    {
        boolean attacked = super.attackEntityAsMob(entity);

        if (attacked && entity instanceof EntityLivingBase) {

            // fire the spectre attack event so witch hats (and other things) can protect against it
            SpectreAttackEvent attackEvent = new SpectreAttackEvent(this, (EntityLivingBase)entity);
            MinecraftForge.EVENT_BUS.post(attackEvent);

            if (attackEvent.getResult() == Event.Result.DENY || attackEvent.isCanceled()) {
                attacked = false;
            }

            if (attacked) {
                if (entity instanceof EntityPlayer) {
                    EnchantmentHelper.applyRandomCurse((EntityPlayer) entity);
                    entity.world.playSound(null, entity.posX, entity.posY, entity.posZ, CharmSounds.SPECTRE_HIT, SoundCategory.HOSTILE, 1.0f, 1.4f - (entity.world.rand.nextFloat() / 2));
                }
            }

            despawn();
        }

        return attacked;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
    {
        // NO OP
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        boolean despawn = false;
        BlockPos blockpos = new BlockPos(this.posX, getEntityBoundingBox().minY, this.posZ);

        int lightFor = world.getLightFor(EnumSkyBlock.SKY, blockpos);
        if (lightFor > Spectre.despawnLight) {
//            Meson.debug("Spectre skylight, going to despawn");
            despawn = true;
        } else {
            int i = world.getLightFromNeighbors(blockpos);
            if (i >= Spectre.despawnLight) {
//                Meson.debug("Spectre neighbour light, going to despawn");
                despawn = true;
            }
        }

        if (Charm.hasFeature(SpectreHaunting.class) && ticksExisted > SpectreHaunting.ticksLiving) {
//            Meson.log("Spectre end of life, going to despawn");
            despawn = true;
        }

        if (despawn) despawn();
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        // don't allow fall or suffocation damage
        if (source == DamageSource.FALL) return false;
        if (source == DamageSource.IN_WALL) return false;
        return super.attackEntityFrom(source, amount);
    }

    @Nullable
    @Override
    protected ResourceLocation getLootTable()
    {
        return LOOT_TABLE;
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return super.getCanSpawnHere() && getEntityBoundingBox().minY <= Spectre.spawnDepth;
    }

    @Override
    public float getEyeHeight()
    {
        return eyeHeight;
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        despawn();
    }

    public void despawn()
    {
        NetworkHandler.INSTANCE.sendToAll(new MessageSpectreDespawn(getPosition()));
        Meson.debug("Spectre despawned " + this.posX + " " + this.posY + " " + this.posZ);
        if (world.getBlockState(getPosition().down()).getBlock() == Blocks.SAND) {
            world.setBlockState(getPosition().down(), Blocks.SOUL_SAND.getDefaultState(), 2);
        }
        setDead();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return CharmSounds.SPECTRE_DEATH;
    }

    @Override
    protected SoundEvent getStepSound()
    {
        return CharmSounds.SPECTRE_MOVE;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return CharmSounds.SPECTRE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return CharmSounds.SPECTRE_HIT;
    }
}

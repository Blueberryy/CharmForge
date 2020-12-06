package svenhjol.charm.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.ChestMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import svenhjol.charm.Charm;
import svenhjol.charm.base.helper.DataBlockHelper;
import svenhjol.charm.base.helper.LootHelper;
import svenhjol.charm.module.EntitySpawners;

import java.util.*;

import static svenhjol.charm.base.helper.DataBlockHelper.getValue;

public class EntitySpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    private final static String ENTITY = "entity";
    private final static String PERSIST = "persist";
    private final static String HEALTH = "health";
    private final static String ARMOR = "armor";
    private final static String EFFECTS = "effects";
    private final static String META = "meta";
    private final static String COUNT = "count";
    private final static String ROTATION = "rotation";

    public ResourceLocation entity = null;
    public Rotation rotation = Rotation.NONE;
    public boolean persist = false;
    public double health = 0;
    public String effects = "";
    public String armor = "";
    public int count = 1;
    public String meta = "";

    public EntitySpawnerTileEntity() {
        super(EntitySpawners.TILE_ENTITY);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);

        this.entity = ResourceLocation.tryCreate(tag.getString(ENTITY));
        this.persist = tag.getBoolean(PERSIST);
        this.health = tag.getDouble(HEALTH);
        this.effects = tag.getString(EFFECTS);
        this.armor = tag.getString(ARMOR);
        this.count = tag.getInt(COUNT);
        this.meta = tag.getString(META);

        String rot = tag.getString(ROTATION);
        this.rotation = rot.isEmpty() ? Rotation.NONE : Rotation.valueOf(rot);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putString(ENTITY, entity.toString());
        tag.putString(ROTATION, rotation.name());
        tag.putBoolean(PERSIST, persist);
        tag.putDouble(HEALTH, health);
        tag.putString(EFFECTS, effects);
        tag.putString(ARMOR, armor);
        tag.putInt(COUNT, count);
        tag.putString(META, meta);

        return tag;
    }

    @Override
    public void tick() {
        if (world == null || world.getGameTime() % 10 == 0 || world.getDifficulty() == Difficulty.PEACEFUL)
            return;

        BlockPos pos = getPos();
        List<PlayerEntity> players = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(pos).grow(EntitySpawners.triggerDistance));

        if (players.size() == 0)
            return;

        // remove the spawner, create the entity
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
        boolean result = trySpawn(pos);

        if (result) {
            Charm.LOG.debug("EntitySpawner spawned entity " + entity.toString() + " at pos: " + pos);
        } else {
            Charm.LOG.debug("EntitySpawner failed to spawn entity " + entity.toString() + " at pos: " + pos);
        }
    }

    public boolean trySpawn(BlockPos pos) {
        Entity spawned;
        if (world == null)
            return false;

        Optional<EntityType<?>> optionalEntityType = Registry.ENTITY_TYPE.getOptional(entity);
        if (!optionalEntityType.isPresent())
            return false;

        EntityType<?> type = optionalEntityType.get();

        if (type == EntityType.MINECART || type == EntityType.CHEST_MINECART)
            return tryCreateMinecart(type, pos);

        if (type == EntityType.ARMOR_STAND)
            return tryCreateArmorStand(pos);

        for (int i = 0; i < this.count; i++) {
            spawned = type.create(world);
            if (spawned == null)
                return false;

            spawned.moveToBlockPosAndAngles(pos, 0.0F, 0.0F);

            if (spawned instanceof MobEntity) {
                MobEntity mob = (MobEntity) spawned;
                if (persist) mob.enablePersistence();

                // set the mob health if specified (values greater than zero)
                if (health > 0) {
                    // need to override this attribute on the entity to allow health values greater than maxhealth
                    ModifiableAttributeInstance healthAttribute = mob.getAttribute(Attributes.MAX_HEALTH);
                    if (healthAttribute != null)
                        healthAttribute.setBaseValue(health);

                    mob.setHealth((float) health);
                }

                // add armor to the mob
                if (!armor.isEmpty()) {
                    Random random = world.rand;
                    tryEquip(mob, armor, random);
                }

                // apply status effects to the mob
                // TODO: make this a helper so that Strange can use it too
                final List<String> effectsList = new ArrayList<>();
                if (effects.length() > 0) {
                    if (effects.contains(",")) {
                        effectsList.addAll(Arrays.asList(effects.split(",")));
                    } else {
                        effectsList.add(effects);
                    }
                    if (effectsList.size() > 0) {
                        effectsList.forEach(effectName -> {
                            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(effectName));
                            if (effect != null)
                                mob.addPotionEffect(new EffectInstance(effect, 999999, 1));
                        });
                    }
                }

                mob.onInitialSpawn((IServerWorld) world, world.getDifficultyForLocation(pos), SpawnReason.TRIGGERED, null, null);
            }

            world.addEntity(spawned);
        }
        return true;
    }

    public boolean tryCreateMinecart(EntityType<?> type, BlockPos pos) {
        AbstractMinecartEntity minecart = null;
        if (world == null) return false;

        if (type == EntityType.CHEST_MINECART) {
            minecart = new ChestMinecartEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

            String loot = getValue("loot", this.meta, "");
            ResourceLocation lootTable = LootHelper.getLootTable(loot, LootTables.CHESTS_ABANDONED_MINESHAFT);
            ((ChestMinecartEntity)minecart).setLootTable(lootTable, world.rand.nextLong());
        } else if (type == EntityType.MINECART) {
            minecart = new MinecartEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        }

        if (minecart == null)
            return false;

        world.addEntity(minecart);

        return true;
    }

    public boolean tryCreateArmorStand(BlockPos pos) {
        if (world == null)
            return false;

        Random random = world.rand;
        ArmorStandEntity stand = EntityType.ARMOR_STAND.create(world);
        if (stand == null)
            return false;

        Direction face = DataBlockHelper.getFacing(DataBlockHelper.getValue("facing", this.meta, "north"));
        Direction facing = this.rotation.rotate(face);
        String type = DataBlockHelper.getValue("type", this.meta, "");

        tryEquip(stand, type, random);

        float yaw = facing.getHorizontalAngle();
        stand.moveToBlockPosAndAngles(pos, yaw, 0.0F);
        world.addEntity(stand);

        return true;
    }

    private void tryEquip(LivingEntity entity, String type, Random random) {
        List<Item> ironHeld = new ArrayList<>(Arrays.asList(
            Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE
        ));

        List<Item> goldHeld = new ArrayList<>(Arrays.asList(
            Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE
        ));

        List<Item> diamondHeld = new ArrayList<>(Arrays.asList(
            Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SHOVEL
        ));

        if (type.equals("leather")) {
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ironHeld.get(random.nextInt(ironHeld.size()))));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.LEATHER_HELMET));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.LEATHER_BOOTS));
        }
        if (type.equals("chain")) {
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ironHeld.get(random.nextInt(ironHeld.size()))));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
        }
        if (type.equals("iron")) {
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(ironHeld.get(random.nextInt(ironHeld.size()))));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.IRON_HELMET));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.IRON_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.IRON_BOOTS));
        }
        if (type.equals("gold")) {
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(goldHeld.get(random.nextInt(goldHeld.size()))));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }
        if (type.equals("diamond")) {
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(diamondHeld.get(random.nextInt(diamondHeld.size()))));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
            if (random.nextFloat() < 0.25F)
                entity.setItemStackToSlot(EquipmentSlotType.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        }
    }
}

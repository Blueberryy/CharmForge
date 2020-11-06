package svenhjol.charm.base.structure;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.DecorationHelper;
import svenhjol.charm.block.BookcaseBlock;
import svenhjol.charm.TileEntity.BookcaseTileEntity;
import svenhjol.charm.TileEntity.EntitySpawnerTileEntity;
import svenhjol.charm.module.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static svenhjol.charm.base.helper.DataBlockHelper.*;
import static svenhjol.charm.base.helper.DecorationHelper.*;

@SuppressWarnings("unused")
public class DataBlockProcessor extends StructureProcessor {
    public DataBlockResolver resolver = new DataBlockResolver();
    public static Map<String, Consumer<DataBlockResolver>> callbacks = new HashMap<>();

    @Nullable
    @Override
    public Template.BlockInfo process(IWorldReader world, BlockPos pos, BlockPos blockPos, Template.BlockInfo unused, Template.BlockInfo blockInfo, PlacementSettings placement, @Nullable Template template) {
        if (blockInfo.state.getBlock() == Blocks.STRUCTURE_BLOCK) {
            StructureMode mode = StructureMode.valueOf(blockInfo.nbt.getString("mode"));
            if (mode == StructureMode.DATA) {
                return resolver.replace(world, placement.getRotation(), blockInfo, new Random(pos.toLong()));
            }
        }

        return blockInfo;
    }

    @Override
    protected IStructureProcessorType<?> getType() {
        return null;
    }

    public static class DataBlockResolver {
        private static final String ANVIL = "anvil";
        private static final String ARMOR = "armor";
        private static final String BLOCK = "block";
        private static final String BOOKSHELF = "bookshelf";
        private static final String CARPET = "carpet";
        private static final String CAULDRON = "cauldron";
        private static final String CHEST = "chest";
        private static final String DECORATION = "decoration";
        private static final String ENTITY = "entity";
        private static final String FLOWER = "flower";
        private static final String LANTERN = "lantern";
        private static final String LAVA = "lava";
        private static final String MOB = "mob";
        private static final String ORE = "ore";
        private static final String FLOWERPOT = "plantpot";
        private static final String RUBBLE = "rubble";
        private static final String RUNE = "rune";
        private static final String SAPLING = "sapling";
        private static final String SPAWNER = "spawner";
        private static final String STORAGE = "storage";

        public static float BLOCK_CHANCE = 0.8F;
        public static float BOOKCASE_CHANCE = 0.25F;
        public static float BOOKCASE_LOOT_CHANCE = 0.1F;
        public static float CHEST_CHANCE = 0.66F;
        public static float DECORATION_CHANCE = 0.85F;
        public static float FLOWER_CHANCE = 0.8F;
        public static float FLOWERPOT_CHANCE = 0.8F;
        public static float LANTERN_CHANCE = 0.9F;
        public static float LANTERN_GOLD_CHANCE = 0.25F;
        public static float LAVA_CHANCE = 0.7F;
        public static float MOB_CHANCE = 0.75F;
        public static float ORE_CHANCE = 0.75F;
        public static float RARE_ORE_CHANCE = 0.25F;
        public static float RARE_CHEST_CHANCE = 0.1F;
        public static float RUBBLE_CHANCE = 0.9F;
        public static float RUNESTONE_CHANCE = 0.75F;
        public static float SAPLING_CHANCE = 0.8F;
        public static float SPAWNER_CHANCE = 0.8F;
        public static float STORAGE_CHANCE = 0.7F;

        public String data;
        public Rotation rotation;
        public BlockState state;
        public BlockPos pos;
        public IWorldReader world;
        public CompoundNBT tag;
        public Random fixedRandom; // fixed according to parent template
        public Random random; // random according to the replaced block hashcode
        public float chance;

        public Template.BlockInfo replace(IWorldReader world, Rotation rotation, Template.BlockInfo blockInfo, Random random) {
            String data = blockInfo.nbt.getString("metadata");
            this.world = world;
            this.fixedRandom = random;
            this.rotation = rotation;
            this.pos = blockInfo.pos;
            this.state = null;
            this.tag = null;
            this.random = new Random(blockInfo.hashCode());

            // pipe character acts as an OR. Data will use one of the definitions at random.
            if (data.contains("|")) {
                String[] split = data.split("\\|");
                data = split[this.random.nextInt(split.length)];
            }

            this.data = data.trim();
            this.chance = getChance(this.data, 0.0F);

            if (this.data.startsWith(ANVIL)) anvil();
            if (this.data.startsWith(ARMOR)) armorStand();
            if (this.data.startsWith(BLOCK)) block();
            if (this.data.startsWith(BOOKSHELF)) bookshelf();
            if (this.data.startsWith(CARPET)) carpet();
            if (this.data.startsWith(CAULDRON)) cauldron();
            if (this.data.startsWith(CHEST)) chest();
            if (this.data.startsWith(DECORATION)) decoration();
            if (this.data.startsWith(ENTITY)) entity();
            if (this.data.startsWith(FLOWER)) flower();
            if (this.data.startsWith(FLOWERPOT)) flowerpot();
            if (this.data.startsWith(LANTERN)) lantern();
            if (this.data.startsWith(LAVA)) lava();
            if (this.data.startsWith(MOB)) mob();
            if (this.data.startsWith(ORE)) ore();
            if (this.data.startsWith(SAPLING)) sapling();
            if (this.data.startsWith(SPAWNER)) spawner();
            if (this.data.startsWith(STORAGE)) storage();

            if (this.state == null) {
                callbacks.entrySet().stream().filter(entry -> this.data.startsWith(entry.getKey())).forEach(entry ->
                    entry.getValue().accept(this));

                if (this.state == null)
                    this.state = Blocks.AIR.getDefaultState();
            }

            return new Template.BlockInfo(this.pos, this.state, this.tag);
        }

        protected void anvil() {
            float f = random.nextFloat();
            if (f < 0.33F) {
                this.state = Blocks.ANVIL.getDefaultState();
            } else if (f < 0.66F) {
                this.state = Blocks.CHIPPED_ANVIL.getDefaultState();
            } else if (f < 1.0F) {
                this.state = Blocks.DAMAGED_ANVIL.getDefaultState();
            }
        }

        protected void armorStand() {
            EntitySpawnerTileEntity tileEntity = EntitySpawner.BLOCK_ENTITY.create();
            if (TileEntity == null) return;
            this.tag = new CompoundNBT();

            TileEntity.entity = new ResourceLocation("minecraft:armor_stand");
            TileEntity.meta = this.data;
            TileEntity.rotation = this.rotation;
            TileEntity.toTag(this.tag);

            this.state = EntitySpawner.ENTITY_SPAWNER.getDefaultState();
        }

        protected void block() {
            if (!withChance(BLOCK_CHANCE)) return;

            String type = getValue("type", this.data, "");
            if (type.isEmpty()) return;

            ResourceLocation typeId = new ResourceLocation(type);
            Optional<Block> optionalBlock = Registry.BLOCK.getOptional(typeId);

            if (!optionalBlock.isPresent())
                return;

            Block block = optionalBlock.get();
            this.state = block.getDefaultState();
        }

        protected void bookshelf() {
            IVariantMaterial variantMaterial = DecorationHelper.getRandomVariantMaterial(fixedRandom);

            if (ModuleHandler.enabled("charm:bookcases") && withChance(BOOKCASE_CHANCE)) {
                state = Bookcases.BOOKCASE_BLOCKS.get(variantMaterial).getDefaultState()
                    .with(BookcaseBlock.SLOTS, BookcaseTileEntity.SIZE); // make it have the "full" texture

                if (random.nextFloat() < BOOKCASE_LOOT_CHANCE) {
                    BookcaseTileEntity tileEntity = Bookcases.TILE_ENTITY.create();
                    if (TileEntity == null)
                        return;

                    TileEntity.setLootTable(DecorationHelper.getRandomLootTable(BOOKCASE_LOOT_TABLES, random), random.nextLong());
                    this.tag = new CompoundNBT();
                    TileEntity.toTag(this.tag);
                }
            } else if (ModuleHandler.enabled("charm:variant_bookshelves") && variantMaterial != VanillaVariantMaterial.OAK) {
                state = VariantBookshelves.BOOKSHELF_BLOCKS.get(variantMaterial).getDefaultState();
            } else {
                state = Blocks.BOOKSHELF.getDefaultState();
            }
        }

        protected void carpet() {
            List<Block> types = new ArrayList<>(DecorationHelper.CARPETS);
            Collections.shuffle(types, fixedRandom);

            int type = getValue("type", this.data, 0);
            if (type > types.size()) type = 0;
            state = types.get(type).getDefaultState();
        }

        protected void cauldron() {
            state = Blocks.CAULDRON.getDefaultState()
                .with(CauldronBlock.LEVEL, (int) Math.max(3.0F, 4.0F * random.nextFloat()));
        }

        protected void chest() {
            if (!withChance(CHEST_CHANCE)) return;

            if (ModuleHandler.enabled("charm:variant_chests")) {
                IVariantMaterial variantMaterial = DecorationHelper.getRandomVariantMaterial(random);

                state = random.nextFloat() < 0.1F ?
                    VariantChests.TRAPPED_CHEST_BLOCKS.get(variantMaterial).getDefaultState() :
                    VariantChests.NORMAL_CHEST_BLOCKS.get(variantMaterial).getDefaultState();

            } else {
                state = Blocks.CHEST.getDefaultState();
            }

            state = setFacing(state, ChestBlock.FACING, getValue("facing", data, "north"));

            ResourceLocation lootTable = DecorationHelper.getRandomLootTable(random.nextFloat() < RARE_CHEST_CHANCE ? RARE_CHEST_LOOT_TABLES : CHEST_LOOT_TABLES, random);
            ChestTileEntity tileEntity = TileEntityType.CHEST.create();
            if (TileEntity == null)
                return;

            TileEntity.setLootTable(getLootTable(data, lootTable), random.nextLong());
            tag = new CompoundNBT();
            TileEntity.write(tag);
        }

        protected void decoration() {
            if (!withChance(DECORATION_CHANCE)) return;

            Direction facing = getFacing(getValue("facing", this.data, "north"));
            state = DecorationHelper.getRandomBlock(DECORATION_BLOCKS, random, facing);
        }

        protected void entity() {
            EntitySpawnerTileEntity tileEntity = EntitySpawner.BLOCK_ENTITY.create();
            if (TileEntity == null) return;
            tag = new CompoundNBT();

            String type = getValue("type", this.data, "");
            if (type.isEmpty()) return;

            ResourceLocation typeId = new ResourceLocation(type);

            if (!Registry.ENTITY_TYPE.getOptional(typeId).isPresent())
                return;

            TileEntity.entity = typeId;
            TileEntity.meta = this.data;
            TileEntity.rotation = this.rotation;
            TileEntity.toTag(this.tag);

            this.state = EntitySpawner.ENTITY_SPAWNER.getDefaultState();
        }

        protected void flower() {
            if (!withChance(FLOWER_CHANCE)) return;
            state = DecorationHelper.getRandomBlock(FLOWERS, random);
        }

        protected void flowerpot() {
            if (!withChance(FLOWERPOT_CHANCE)) return;
            state = DecorationHelper.getRandomBlock(FLOWER_POTS, random);
        }

        protected void lantern() {
            if (!withChance(LANTERN_CHANCE)) return;
            state = Blocks.LANTERN.getDefaultState();

            if (ModuleHandler.enabled("charm:gold_lanterns") && random.nextFloat() < LANTERN_GOLD_CHANCE)
                state = GoldLanterns.GOLD_LANTERN.getDefaultState();

            if (data.contains("hanging"))
                state = state.with(LanternBlock.HANGING, true);
        }

        protected void lava() {
            state = Blocks.MAGMA_BLOCK.getDefaultState();

            if (fixedRandom.nextFloat() < LAVA_CHANCE)
                state = Blocks.LAVA.getDefaultState();
        }

        protected void mob() {
            if (!withChance(MOB_CHANCE)) return;

            EntitySpawnerTileEntity tileEntity = EntitySpawner.BLOCK_ENTITY.create();
            if (TileEntity == null) return;

            String type = getValue("type", this.data, "");
            if (type.isEmpty()) return;
            tag = new CompoundNBT();

            TileEntity.entity = new ResourceLocation(type);
            TileEntity.health = getValue("health", this.data, 0.0D);
            TileEntity.persist = getValue("persist", this.data, true);
            TileEntity.count = getValue("count", this.data, 1);
            TileEntity.rotation = this.rotation;
            TileEntity.toTag(this.tag);

            this.state = EntitySpawner.ENTITY_SPAWNER.getDefaultState();
        }

        protected void ore() {
            if (!withChance(ORE_CHANCE)) return;

            String type = getValue("type", this.data, "");
            if (!type.isEmpty()) {
                ResourceLocation typeId = new ResourceLocation(type);
                if (!Registry.ENTITY_TYPE.getOptional(typeId).isPresent())
                    return;

                Block ore = Registry.BLOCK.getOrDefault(typeId);
                state = ore.getDefaultState();
                return;
            }

            state = fixedRandom.nextFloat() < RARE_ORE_CHANCE ?
                DecorationHelper.getRandomBlock(RARE_ORES, fixedRandom) :
                DecorationHelper.getRandomBlock(COMMON_ORES, fixedRandom);
        }

        protected void sapling() {
            if (!withChance(SAPLING_CHANCE)) return;
            state = DecorationHelper.getRandomBlock(SAPLINGS, random);
        }

        protected void spawner() {
            if (!withChance(SPAWNER_CHANCE)) return;

            EntityType<?> entity;
            String type = getValue("type", this.data, "");
            if (type.isEmpty()) {
                // get random spawner mob
                entity = SPAWNER_MOBS.size() > 0 ? SPAWNER_MOBS.get(random.nextInt(SPAWNER_MOBS.size())) : null;
            } else {
                // try and use the specified entity
                ResourceLocation typeId = new ResourceLocation(type);
                if (!Registry.ENTITY_TYPE.getOptional(typeId).isPresent())
                    return;

                entity = Registry.ENTITY_TYPE.getOrDefault(typeId);
            }

            if (entity == null)
                return;

            state = Blocks.SPAWNER.getDefaultState();

            MobSpawnerTileEntity tileEntity = TileEntityType.MOB_SPAWNER.create();
            if (TileEntity != null) {
                TileEntity.getSpawnerBaseLogic().setEntityType(entity);
                tag = new CompoundNBT();
                TileEntity.write(this.tag);
            }
        }

        protected void storage() {
            if (!withChance(STORAGE_CHANCE)) return;

            LockableLootTileEntity tileEntity;
            IVariantMaterial woodType = DecorationHelper.getRandomVariantMaterial(random);

            if (random.nextFloat() < 0.5F && ModuleHandler.enabled("charm:crates")) {
                // get a crate
                state = Crates.CRATE_BLOCKS.get(woodType).getDefaultState();
                TileEntity = Crates.BLOCK_ENTITY.create();
            } else {
                // get a barrel
                if (ModuleHandler.enabled("charm:variant_barrels")) {
                    // get variant barrel
                    state = VariantBarrels.BARREL_BLOCKS.get(woodType).getDefaultState();
                } else {
                    // get vanilla barrel
                    state = Blocks.BARREL.getDefaultState();
                }
                state = state.with(BarrelBlock.PROPERTY_FACING, Direction.UP);
                TileEntity = TileEntityType.BARREL.create();
            }

            if (TileEntity == null)
                return;

            ResourceLocation lootTable = DecorationHelper.getRandomLootTable(COMMON_LOOT_TABLES, random);
            TileEntity.setLootTable(getLootTable(data, lootTable), random.nextLong());
            tag = new CompoundNBT();
            TileEntity.write(tag);
        }

        public boolean withChance(float chance) {
            float f = this.random.nextFloat();
            return this.chance > 0 ? f < this.chance : f < chance;
        }

        public float getChance(String data, float fallback) {
            int i = getValue("chance", data, 0);
            return i == 0 ? fallback : ((float) i) / 100.0F;
        }
    }
}

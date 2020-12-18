package svenhjol.charm.base.handler;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import svenhjol.charm.base.CharmLoot;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.helper.LootHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static svenhjol.charm.base.helper.DecorationHelper.*;

public class DecorationHandler {
    public static List<StructureProcessor> SINGLE_POOL_ELEMENT_PROCESSORS = new ArrayList<>();

    private static boolean hasInit = false;

    public static void init() {
        if (hasInit)
            return;

        CARPETS.addAll(BlockTags.CARPETS.getAllElements());
        FLOWERS.addAll(BlockTags.FLOWERS.getAllElements().stream()
            .filter(b -> b != Blocks.WITHER_ROSE).collect(Collectors.toList()));
        FLOWER_POTS.addAll(BlockTags.FLOWER_POTS.getAllElements());
        SAPLINGS.addAll(BlockTags.SAPLINGS.getAllElements());
        WOOL.addAll(BlockTags.WOOL.getAllElements());

        VARIANT_MATERIALS.addAll(VanillaVariantMaterial.getTypes());

        CHEST_LOOT_TABLES = Arrays.asList(
            LootTables.CHESTS_ABANDONED_MINESHAFT,
            LootTables.CHESTS_BURIED_TREASURE,
            LootTables.CHESTS_DESERT_PYRAMID,
            LootTables.CHESTS_JUNGLE_TEMPLE,
            LootTables.CHESTS_SIMPLE_DUNGEON,
            LootTables.CHESTS_STRONGHOLD_CORRIDOR,
            LootTables.CHESTS_STRONGHOLD_CROSSING,
            LootTables.CHESTS_SHIPWRECK_SUPPLY,
            LootTables.CHESTS_SHIPWRECK_TREASURE
        );

        COMMON_LOOT_TABLES.addAll(Arrays.asList(
            LootTables.CHESTS_JUNGLE_TEMPLE_DISPENSER,
            LootTables.CHESTS_IGLOO_CHEST
        ));

        COMMON_LOOT_TABLES.addAll(LootHelper.getVanillaVillageLootTables());

        RARE_CHEST_LOOT_TABLES.addAll(Arrays.asList(
            LootTables.RUINED_PORTAL
        ));

        BOOKCASE_LOOT_TABLES.addAll(Arrays.asList(
            CharmLoot.VILLAGE_LIBRARIAN
        ));

        RARE_BOOKCASE_LOOT_TABLES.addAll(Arrays.asList(
            LootTables.CHESTS_STRONGHOLD_LIBRARY
        ));

        COMMON_ORES.addAll(Arrays.asList(
            Blocks.IRON_ORE,
            Blocks.COAL_ORE,
            Blocks.REDSTONE_ORE
        ));

        RARE_ORES.addAll(Arrays.asList(
            Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE
        ));

        DECORATION_BLOCKS.addAll(Arrays.asList(
            Blocks.ANVIL,
            Blocks.BELL,
            Blocks.BLAST_FURNACE,
            Blocks.BONE_BLOCK,
            Blocks.BREWING_STAND,
            Blocks.CARTOGRAPHY_TABLE,
            Blocks.CARVED_PUMPKIN,
            Blocks.CAULDRON,
            Blocks.CHIPPED_ANVIL,
            Blocks.COAL_BLOCK,
            Blocks.COBWEB,
            Blocks.COMPOSTER,
            Blocks.CRAFTING_TABLE,
            Blocks.DAMAGED_ANVIL,
            Blocks.DISPENSER,
            Blocks.FLETCHING_TABLE,
            Blocks.FURNACE,
            Blocks.HAY_BLOCK,
            Blocks.IRON_BLOCK,
            Blocks.JUKEBOX,
            Blocks.LANTERN,
            Blocks.LAPIS_BLOCK,
            Blocks.LECTERN,
            Blocks.MELON,
            Blocks.NOTE_BLOCK,
            Blocks.OBSERVER,
            Blocks.POLISHED_ANDESITE,
            Blocks.POLISHED_DIORITE,
            Blocks.POLISHED_GRANITE,
            Blocks.PUMPKIN,
            Blocks.SLIME_BLOCK,
            Blocks.SMITHING_TABLE,
            Blocks.SMOKER,
            Blocks.STONECUTTER
        ));

        DECORATION_BLOCKS.addAll(WOOL);
        DECORATION_BLOCKS.addAll(FLOWER_POTS);

        SPAWNER_MOBS.addAll(Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CAVE_SPIDER
        ));

        hasInit = true;
    }
}

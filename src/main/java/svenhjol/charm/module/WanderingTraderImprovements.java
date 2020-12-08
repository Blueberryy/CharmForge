package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.BiomeHelper;
import svenhjol.charm.base.helper.MapHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Module(mod = Charm.MOD_ID, description = "Wandering traders only appear near signal campfires and sell maps to biomes and structures.", hasSubscriptions = true)
public class WanderingTraderImprovements extends CharmModule {
    public static final List<TraderMap> traderMaps = new ArrayList<>();

    @Config(name = "Trade biome maps", description = "If true, wandering traders will sell maps to biomes.")
    public static boolean tradeBiomeMaps = true;

    @Config(name = "Trade structure maps", description = "If true, wandering traders will sell maps to structures.")
    public static boolean tradeStructureMaps = true;

    @Config(name = "Frequent spawning", description = "If true, makes wandering traders more likely to spawn after one Minecraft day.")
    public static boolean frequentSpawn = false;

    @Override
    public void init() {
        if (tradeStructureMaps) {
            traderMaps.addAll(Arrays.asList(
                new StructureMap(Structure.RUINED_PORTAL, false), // ruined portal
                new StructureMap(Structure.VILLAGE, false), // village
                new StructureMap(Structure.SWAMP_HUT, false), // swamp hut
                new StructureMap(Structure.SHIPWRECK, false), // shipwreck
                new StructureMap(Structure.OCEAN_RUIN, false), // ocean ruin
                new StructureMap(Structure.PILLAGER_OUTPOST, false), // pillager outpost
                new StructureMap(Structure.MINESHAFT, false), // mineshaft
                new StructureMap(Structure.IGLOO, false), // igloo
                new StructureMap(Structure.JUNGLE_PYRAMID, true), // jungle temple
                new StructureMap(Structure.DESERT_PYRAMID, true) // desert pyramid
            ));
        }

        if (tradeBiomeMaps) {
            traderMaps.addAll(Arrays.asList(
                new BiomeMap(Biomes.WARM_OCEAN, false),
                new BiomeMap(Biomes.SNOWY_TUNDRA, false),
                new BiomeMap(Biomes.DESERT, false),
                new BiomeMap(Biomes.SUNFLOWER_PLAINS, false),
                new BiomeMap(Biomes.FROZEN_OCEAN, false),
                new BiomeMap(Biomes.BADLANDS, true),
                new BiomeMap(Biomes.FLOWER_FOREST, true),
                new BiomeMap(Biomes.MUSHROOM_FIELDS, true),
                new BiomeMap(Biomes.BAMBOO_JUNGLE, true),
                new BiomeMap(Biomes.ICE_SPIKES, true)
            ));
        }
    }

    @SubscribeEvent
    public void onWandererTrades(WandererTradesEvent event) {
        for (int i = 0; i < 3; i++) {
            event.getGenericTrades().add(new StructureMapForEmeraldsTrade());
            event.getRareTrades().add(new StructureMapForEmeraldsTrade());
        }
    }

    public static boolean shouldSpawnFrequently() {
        return ModuleHandler.enabled("charm:wandering_trader_improvements") && frequentSpawn;
    }

    public static class StructureMapForEmeraldsTrade implements VillagerTrades.ITrade {
        @Override
        public MerchantOffer getOffer(Entity trader, Random rand) {
            TraderMap traderMap = traderMaps.get(rand.nextInt(traderMaps.size()));

            if (!trader.world.isRemote) {
                ItemStack map = traderMap.getMap((ServerWorld) trader.world, trader.getPosition());
                if (map != null) {
                    ItemStack in1 = new ItemStack(Items.EMERALD, traderMap.getCost(rand));
                    ItemStack in2 = new ItemStack(Items.COMPASS);
                    return new MerchantOffer(in1, in2, map, 1, 5, 0.2F);
                }
            }

            return null;
        }
    }

    public interface TraderMap {
        ItemStack getMap(ServerWorld world, BlockPos pos);

        int getCost(Random rand);
    }

    public static class StructureMap implements TraderMap {
        public Structure<?> structure;
        public boolean rare;

        public StructureMap(Structure<?> structure, boolean rare) {
            this.structure = structure;
            this.rare = rare;
        }

        @Override
        public ItemStack getMap(ServerWorld world, BlockPos pos) {
            int color = 0x662200;
            BlockPos nearestStructure = world.func_241117_a_(structure, pos, 2000, true);
            if (nearestStructure == null)
                return null;

            TranslationTextComponent structureName = new TranslationTextComponent("structure.charm." + structure.getStructureName());
            TranslationTextComponent mapName = new TranslationTextComponent("filled_map.charm.trader_map", structureName);
            return MapHelper.getMap(world, nearestStructure, mapName, MapDecoration.Type.TARGET_X, color);
        }

        @Override
        public int getCost(Random rand) {
            return rare ? rand.nextInt(4) + 6 : rand.nextInt(2) + 2;
        }
    }

    public static class BiomeMap implements TraderMap {
        public RegistryKey<Biome> biomeKey;
        public boolean rare;

        public BiomeMap(RegistryKey<Biome> biomeKey, boolean rare) {
            this.biomeKey = biomeKey;
            this.rare = rare;
        }

        @Override
        public ItemStack getMap(ServerWorld world, BlockPos pos) {
            int color = 0x002266;

            BlockPos nearestBiome = BiomeHelper.locateBiome(biomeKey, world, pos);
            String biomeName = biomeKey.getLocation().getPath();

            if (nearestBiome == null)
                return null;

            TranslationTextComponent mapName = new TranslationTextComponent("filled_map.charm.trader_map", new TranslationTextComponent("biome.minecraft." + biomeName));
            return MapHelper.getMap(world, nearestBiome, mapName, MapDecoration.Type.TARGET_X, color);
        }

        @Override
        public int getCost(Random rand) {
            return rare ? rand.nextInt(3) + 3 : rand.nextInt(1) + 1;
        }
    }
}

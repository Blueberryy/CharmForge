package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.BiomeHelper;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Villages can spawn in swamps and jungles.")
public class MoreVillageBiomes extends CharmModule {
    @Override
    public void init() {
        List<RegistryKey<Biome>> plainsBiomes = new ArrayList<>(Arrays.asList(
            Biomes.JUNGLE, Biomes.BAMBOO_JUNGLE, Biomes.SWAMP
        ));

        List<RegistryKey<Biome>> taigaBiomes = new ArrayList<>(Arrays.asList(
            Biomes.SNOWY_TAIGA
        ));

        List<RegistryKey<Biome>> snowyBiomes = new ArrayList<>(Arrays.asList(
            Biomes.ICE_SPIKES
        ));

        for (RegistryKey<Biome> biomeKey : plainsBiomes) {
            Biome biome = BiomeHelper.getBiomeFromBiomeKey(biomeKey);
            BiomeHelper.addStructureFeature(biome, ConfiguredStructureFeatures.VILLAGE_PLAINS);
        }

        for (RegistryKey<Biome> biomeKey : taigaBiomes) {
            Biome biome = BiomeHelper.getBiomeFromBiomeKey(biomeKey);
            BiomeHelper.addStructureFeature(biome, ConfiguredStructureFeatures.VILLAGE_TAIGA);
        }

        for (RegistryKey<Biome> biomeKey : snowyBiomes) {
            Biome biome = BiomeHelper.getBiomeFromBiomeKey(biomeKey);
            BiomeHelper.addStructureFeature(biome, ConfiguredStructureFeatures.VILLAGE_SNOWY);
        }

        AddEntityCallback.EVENT.register(this::changeVillagerSkin);
    }

    private ActionResult changeVillagerSkin(Entity entity) {
        if (!entity.world.isRemote
            && entity instanceof VillagerEntity
            && entity.updateNeeded
            && entity.age == 0
        ) {
            VillagerEntity villager = (VillagerEntity) entity;
            VillagerData data = villager.getVillagerData();
            ServerWorld world = (ServerWorld)entity.world;

            if (data.getType() == VillagerType.PLAINS) {
                Biome biome = BiomeHelper.getBiome(world, villager.getPosition());
                Biome.Category category = biome.getCategory();

                if (category.equals(Biome.Category.JUNGLE) || category.equals(Biome.Category.SWAMP))
                    villager.setVillagerData(data.withType(VillagerType.forBiome(BiomeHelper.getBiomeKeyAtPosition(world, villager.getBlockPos()))));
            }
        }

        return ActionResult.PASS;
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.helper.BiomeHelper;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Module(mod = Charm.MOD_ID, hasSubscriptions = true, description = "Villages can spawn in swamps and jungles.")
public class MoreVillageBiomes extends CharmModule {
    private List<ResourceLocation> plainsBiomes = new ArrayList<>();
    private List<ResourceLocation> taigaBiomes = new ArrayList<>();
    private List<ResourceLocation> snowyBiomes = new ArrayList<>();

    @Override
    public void init() {
        plainsBiomes = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:jungle"),
            new ResourceLocation("minecraft:bamboo_jungle"),
            new ResourceLocation("minecraft:swamp")
        ));

        taigaBiomes = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:snowy_taiga")
        ));

        snowyBiomes = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:ice_spikes")
        ));
    }

    @SubscribeEvent
    public void onVillagerJoinWorld(EntityJoinWorldEvent event) {
        if (!event.isCanceled())
            changeVillagerSkin(event.getEntity());
    }

    @SubscribeEvent
    public void onBiomeLoading(BiomeLoadingEvent event) {
        if (!event.isCanceled())
            tryAddStructureToBiome(event);
    }

    private void changeVillagerSkin(Entity entity) {
        if (!entity.world.isRemote
            && entity instanceof VillagerEntity
            && entity.addedToChunk
            && entity.ticksExisted == 0
        ) {
            VillagerEntity villager = (VillagerEntity) entity;
            VillagerData data = villager.getVillagerData();
            ServerWorld world = (ServerWorld)entity.world;

            if (data.getType() == VillagerType.PLAINS) {
                Biome biome = BiomeHelper.getBiome(world, villager.getPosition());
                Biome.Category category = biome.getCategory();

                if (category.equals(Biome.Category.JUNGLE) || category.equals(Biome.Category.SWAMP))
                    villager.setVillagerData(data.withType(VillagerType.func_242371_a(BiomeHelper.getBiomeKeyAtPosition(world, villager.getPosition()))));
            }
        }
    }

    private void tryAddStructureToBiome(BiomeLoadingEvent event) {
        if (event.getName() == null)
            return;

        List<Supplier<StructureFeature<?, ?>>> structures = event.getGeneration().getStructures();

        ResourceLocation biomeId = event.getName();
        if (plainsBiomes.contains(biomeId)) {
            structures.add(() -> StructureFeatures.VILLAGE_PLAINS);
        } else if (taigaBiomes.contains(biomeId)) {
            structures.add(() -> StructureFeatures.VILLAGE_TAIGA);
        } else if (snowyBiomes.contains(biomeId)) {
            structures.add(() -> StructureFeatures.VILLAGE_SNOWY);
        }
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.BiomeHelper;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.MoobloomsClient;
import svenhjol.charm.entity.MoobloomEntity;
import svenhjol.charm.entity.goal.BeeMoveToMoobloomGoal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Module(mod = Charm.MOD_ID, client = MoobloomsClient.class, hasSubscriptions = true, description = "Mooblooms are cow-like mobs that come in a variety of flower types. They spawn flowers where they walk and can be milked for suspicious stew.")
public class Mooblooms extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "moobloom");
    public static EntityType<MoobloomEntity> MOOBLOOM;

    @Override
    public void register() {
        MOOBLOOM = RegistryHandler.entity(ID, EntityType.Builder.create(MoobloomEntity::new, EntityClassification.CREATURE)
            .size(0.9F, 1.4F)
            .trackingRange(10)
            .build(ID.getPath()));

        EntitySpawnPlacementRegistry.register(MOOBLOOM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MoobloomEntity::canSpawn);
    }

    @Override
    public void init() {
        MobHelper.setEntityAttributes(MOOBLOOM, CowEntity.registerAttributes().create());

        // add the mooblooms to flower forest biomes
        List<RegistryKey<Biome>> biomes = new ArrayList<>(Collections.singletonList(Biomes.FLOWER_FOREST));

        biomes.forEach(biomeKey -> {
            Biome biome = BiomeHelper.getBiomeFromBiomeKey(biomeKey);
            BiomeHelper.addSpawnEntry(biome, EntityClassification.CREATURE, MOOBLOOM, 30, 2, 4);
        });
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.isCanceled())
            tryAddGoalsToBee(event.getEntity());
    }

    private void tryAddGoalsToBee(Entity entity) {
        if (entity instanceof BeeEntity) {
            BeeEntity bee = (BeeEntity)entity;
            if (MobHelper.getGoals(bee).stream().noneMatch(g -> g.getGoal() instanceof BeeMoveToMoobloomGoal))
                MobHelper.getGoalSelector(bee).addGoal(4, new BeeMoveToMoobloomGoal(bee));
        }
    }
}

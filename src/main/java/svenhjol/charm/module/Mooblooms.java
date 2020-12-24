package svenhjol.charm.module;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
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

    private List<ResourceLocation> biomes;

    @Override
    public void register() {
        MOOBLOOM = RegistryHandler.entity(ID, EntityType.Builder.create(MoobloomEntity::new, EntityClassification.CREATURE)
            .size(0.9F, 1.4F)
            .trackingRange(10)
            .build(ID.getPath()));

        EntitySpawnPlacementRegistry.register(MOOBLOOM, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MoobloomEntity::canSpawn);
    }

    @Override
    public void onCommonSetup(FMLCommonSetupEvent event) {
        MobHelper.setEntityAttributes(MOOBLOOM, CowEntity.func_234188_eI_().create());
    }

    @Override
    public void init() {
        // add the mooblooms to flower forest biomes
        biomes = new ArrayList<>(Collections.singletonList(new ResourceLocation("minecraft:flower_forest")));
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.isCanceled())
            tryAddGoalsToBee(event.getEntity());
    }

    @SubscribeEvent
    public void on(BiomeLoadingEvent event) {
        if (!event.isCanceled())
            tryAddEntityToSpawn(event);
    }

    private void tryAddGoalsToBee(Entity entity) {
        if (entity instanceof BeeEntity) {
            BeeEntity bee = (BeeEntity)entity;
            if (MobHelper.getGoals(bee).stream().noneMatch(g -> g.getGoal() instanceof BeeMoveToMoobloomGoal))
                MobHelper.getGoalSelector(bee).addGoal(4, new BeeMoveToMoobloomGoal(bee));
        }
    }

    private void tryAddEntityToSpawn(BiomeLoadingEvent event) {
        if (event.getName() == null)
            return;

        if (!biomes.contains(event.getName()))
            return;

        List<MobSpawnInfo.Spawners> spawner = event.getSpawns().getSpawner(EntityClassification.CREATURE);
        spawner.add(new MobSpawnInfo.Spawners(MOOBLOOM, 30, 5, 6));
    }
}

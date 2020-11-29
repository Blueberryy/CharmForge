package svenhjol.charm.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.CoralSquidsClient;
import svenhjol.charm.entity.CoralSquidEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, client = CoralSquidsClient.class, hasSubscriptions = true, description = "Coral Squids spawn near coral in warm oceans.")
public class CoralSquids extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "coral_squid");
    public static ResourceLocation EGG_ID = new ResourceLocation(Charm.MOD_ID, "coral_squid_spawn_egg");

    public static EntityType<CoralSquidEntity> CORAL_SQUID;
    public static Item SPAWN_EGG;

    public CoralSquidsClient client;

    @Config(name = "Drop chance", description = "Chance (out of 1.0) of a coral squid dropping coral when killed by the player.")
    public static double dropChance = 0.2D;

    @Config(name = "Spawn weight", description = "Chance of coral squids spawning in warm ocean biomes.")
    public static int spawnWeight = 50;

    private List<ResourceLocation> biomes = new ArrayList<>();

    @Override
    public void register() {
        CORAL_SQUID = RegistryHandler.entity(ID, EntityType.Builder.create(CoralSquidEntity::new, EntityClassification.WATER_AMBIENT)
            .size(0.54F, 0.54F)
            .trackingRange(8)
            .build(ID.getPath()));

        SPAWN_EGG = RegistryHandler.item(EGG_ID, new SpawnEggItem(CORAL_SQUID, 0x0000FF, 0xFF00FF, (new Item.Properties()).group(ItemGroup.MISC)));

        EntitySpawnPlacementRegistry.register(CORAL_SQUID, EntitySpawnPlacementRegistry.PlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, CoralSquidEntity::canSpawn);
    }

    @Override
    public void init() {
        MobHelper.setEntityAttributes(CORAL_SQUID, CoralSquidEntity.createSquidAttributes().create());

        biomes = new ArrayList<>(Arrays.asList(
            new ResourceLocation("minecraft:warm_ocean"),
            new ResourceLocation("minecraft:deep_warn_ocean")
        ));
    }

    @SubscribeEvent
    public void on(BiomeLoadingEvent event) {
        if (!event.isCanceled())
            tryAddEntityToSpawn(event);
    }

    private void tryAddEntityToSpawn(BiomeLoadingEvent event) {
        if (event.getName() == null)
            return;

        if (!biomes.contains(event.getName()))
            return;

        List<MobSpawnInfo.Spawners> spawner = event.getSpawns().getSpawner(EntityClassification.WATER_AMBIENT);
        spawner.add(new MobSpawnInfo.Spawners(CORAL_SQUID, spawnWeight, 5, 6));
    }
}

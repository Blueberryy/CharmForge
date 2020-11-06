package svenhjol.charm.module;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.BiomeHelper;
import svenhjol.charm.base.helper.MobHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.CoralSquidsClient;
import svenhjol.charm.entity.CoralSquidEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Coral Squids spawn near coral in warm oceans.")
public class CoralSquids extends CharmModule {
    public static ResourceLocation ID = new ResourceLocation(Charm.MOD_ID, "coral_squid");
    public static ResourceLocation EGG_ID = new ResourceLocation(Charm.MOD_ID, "coral_squid_spawn_egg");

    public static EntityType<CoralSquidEntity> CORAL_SQUID;
    public static Item SPAWN_EGG;

    public CoralSquidsClient client;

    @Config(name = "Drop chance", description = "Chance (out of 1.0) of a coral squid dropping coral when killed by the player.")
    public static double dropChance = 0.2D;

    @Override
    public void register() {
        CORAL_SQUID = RegistryHandler.entity(ID, EntityType.Builder.create(CoralSquidEntity::new, EntityClassification.WATER_CREATURE)
            .size(0.4F, 0.4F)
            .trackingRange(8)
            .build(ID.getPath()));

        SPAWN_EGG = RegistryHandler.item(EGG_ID, new SpawnEggItem(CORAL_SQUID, 0x0000FF, 0xFF00FF, (new Item.Properties()).group(ItemGroup.MISC)));

        MobHelper.setEntityAttributes(CORAL_SQUID, CoralSquidEntity.createSquidAttributes().create());
    }

    @Override
    public void clientRegister() {
        client = new CoralSquidsClient(this);
    }

    @Override
    public void init() {
        List<RegistryKey<Biome>> biomes = new ArrayList<>(Arrays.asList(Biomes.WARM_OCEAN, Biomes.DEEP_WARM_OCEAN));

        biomes.forEach(biomeKey -> {
            Biome biome = BiomeHelper.getBiomeFromBiomeKey(biomeKey);
            BiomeHelper.addSpawnEntry(biome, EntityClassification.WATER_AMBIENT, CORAL_SQUID, 40, 5, 6);
        });
    }
}

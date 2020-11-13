package svenhjol.charm.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.ICharmEnum;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.module.VariantMobTextures;
import svenhjol.charm.render.VariantMobRenderer;

import java.util.*;

public class VariantMobTexturesClient extends CharmClientModule {
    private static final String PREFIX = "textures/entity/";
    private static final ResourceLocation DEFAULT_SHEEP = new ResourceLocation(PREFIX + "sheep/sheep.png");

    public static List<ResourceLocation> chickens = new ArrayList<>();
    public static List<ResourceLocation> cows = new ArrayList<>();
    public static List<ResourceLocation> snowGolems = new ArrayList<>();
    public static List<ResourceLocation> squids = new ArrayList<>();
    public static List<ResourceLocation> pigs = new ArrayList<>();
    public static List<ResourceLocation> wolves = new ArrayList<>();

    public static List<ResourceLocation> rareChickens = new ArrayList<>();
    public static List<ResourceLocation> rareCows = new ArrayList<>();
    public static List<ResourceLocation> rareSquids = new ArrayList<>();
    public static List<ResourceLocation> rarePigs = new ArrayList<>();
    public static List<ResourceLocation> rareWolves = new ArrayList<>();

    public static Map<ResourceLocation, ResourceLocation> wolvesTame = new HashMap<>();
    public static Map<ResourceLocation, ResourceLocation> wolvesAngry = new HashMap<>();
    public static Map<DyeColor, ResourceLocation> sheep = new HashMap<>();

    public VariantMobTexturesClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        if (!enabled)
            return;

        if (VariantMobTextures.variantChickens && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.CHICKEN, VariantMobRenderer.Chicken::new);

        if (VariantMobTextures.variantCows && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.COW, VariantMobRenderer.Cow::new);

        if (VariantMobTextures.variantPigs && !ModHelper.isLoaded("quark") || VariantMobTextures.override)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.PIG, VariantMobRenderer.Pig::new);

        if (VariantMobTextures.variantSheep)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SHEEP, VariantMobRenderer.Sheep::new);

        if (VariantMobTextures.variantSnowGolems)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SNOW_GOLEM, VariantMobRenderer.SnowGolem::new);

        if (VariantMobTextures.variantSquids)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.SQUID, VariantMobRenderer.Squid::new);

        if (VariantMobTextures.variantWolves)
            RenderingRegistry.registerEntityRenderingHandler(EntityType.WOLF, VariantMobRenderer.Wolf::new);
    }

    @Override
    public void init() {
        // reset
        chickens = new ArrayList<>();
        cows = new ArrayList<>();
        pigs = new ArrayList<>();
        snowGolems = new ArrayList<>();
        squids = new ArrayList<>();
        wolves = new ArrayList<>();
        wolvesTame = new HashMap<>();
        wolvesAngry = new HashMap<>();

        rareChickens = new ArrayList<>();
        rareCows = new ArrayList<>();
        rarePigs = new ArrayList<>();
        rareSquids = new ArrayList<>();
        rareWolves = new ArrayList<>();

        // add vanilla textures
        chickens.add(new ResourceLocation(PREFIX + "chicken.png"));
        cows.add(new ResourceLocation(PREFIX + "cow/cow.png"));
        pigs.add(new ResourceLocation(PREFIX + "pig/pig.png"));
        snowGolems.add(new ResourceLocation(PREFIX + "snow_golem.png"));
        squids.add(new ResourceLocation(PREFIX + "squid.png"));

        ResourceLocation wolf = new ResourceLocation(PREFIX + "wolf/wolf.png");
        wolves.add(wolf);
        wolvesTame.put(wolf, new ResourceLocation(PREFIX + "wolf/wolf_tame.png"));
        wolvesAngry.put(wolf, new ResourceLocation(PREFIX + "wolf/wolf_angry.png"));

        for (int i = 1; i <= 5; i++)
            addCustomTextures(chickens, MobType.CHICKEN, "chicken" + i);

        for (int i = 1; i <= 2; i++)
            addCustomTextures(rareChickens, MobType.CHICKEN, "rare_chicken" + i);

        for (int i = 1; i <= 7; i++)
            addCustomTextures(cows, MobType.COW, "cow" + i);

        for (int i = 1; i <= 1; i++)
            addCustomTextures(rareCows, MobType.COW, "rare_cow" + i);

        for (int i = 1; i <= 5; i++)
            addCustomTextures(pigs, MobType.PIG, "pig" + i);

        for (int i = 1; i <= 1; i++)
            addCustomTextures(rarePigs, MobType.PIG, "rare_pig" + i);

        for (int i = 1; i <= 5; i++)
            addCustomTextures(snowGolems, MobType.SNOW_GOLEM, "snow_golem" + i);

        for (int i = 1; i <= 4; i++)
            addCustomTextures(squids, MobType.SQUID, "squid" + i);

        for (int i = 1; i <= 1; i++)
            addCustomTextures(rareSquids, MobType.SQUID, "rare_squid" + i);

        for (int i = 1; i <= 25; i++)
            addCustomTextures(wolves, MobType.WOLF, "nlg_wolf" + i); // add NeverLoseGuy wolf textures

        for (int i = 1; i <= 1; i++)
            addCustomTextures(rareWolves, MobType.WOLF, "rare_wolf" + i);

        addCustomTextures(wolves, MobType.WOLF, "brownwolf", "greywolf", "blackwolf", "amotwolf", "jupiter1390");

        // add all the sheep textures by dyecolor
        for (DyeColor color : DyeColor.values()) {
            ResourceLocation res = createResource(MobType.SHEEP, "sheep_" + color.toString());
            sheep.put(color, res);
        }
    }

    public void addCustomTextures(List<ResourceLocation> set, MobType type, String... names) {
        ArrayList<String> textures = new ArrayList<>(Arrays.asList(names));

        textures.forEach(texture -> {
            ResourceLocation res = createResource(type, texture);
            set.add(res);

            if (type == MobType.WOLF) {
                wolvesTame.put(res, createResource(type, texture + "_tame"));
                wolvesAngry.put(res, createResource(type, texture + "_angry"));
            }
        });
    }

    public static ResourceLocation getChickenTexture(ChickenEntity entity) {
        return getRandomTexture(entity, chickens, rareChickens);
    }

    public static ResourceLocation getCowTexture(CowEntity entity) {
        return getRandomTexture(entity, cows, rareCows);
    }

    public static ResourceLocation getPigTexture(PigEntity entity) {
        return getRandomTexture(entity, pigs, rarePigs);
    }

    public static ResourceLocation getSheepTexture(SheepEntity entity) {
        DyeColor fleeceColor = entity.getFleeceColor();
        return sheep.getOrDefault(fleeceColor, DEFAULT_SHEEP);
    }

    public static ResourceLocation getSnowGolemTexture(SnowGolemEntity entity) {
        return getRandomTexture(entity, snowGolems, ImmutableList.of());
    }

    public static ResourceLocation getSquidTexture(SquidEntity entity) {
        return getRandomTexture(entity, squids, rareSquids);
    }

    public static ResourceLocation getWolfTexture(WolfEntity entity) {
        ResourceLocation res = getRandomTexture(entity, wolves, rareWolves);

        if (entity.isTamed()) {
            res = wolvesTame.get(res);
        } else if (entity.func_241357_a_(entity.world)) {
            res = wolvesAngry.get(res);
        }

        return res;
    }

    public static ResourceLocation getRandomTexture(Entity entity, List<ResourceLocation> normalSet, List<ResourceLocation> rareSet) {
        UUID id = entity.getUniqueID();
        boolean isRare = VariantMobTextures.rareVariants && !rareSet.isEmpty() && (id.getLeastSignificantBits() + id.getMostSignificantBits()) % VariantMobTextures.rarity == 0;

        List<ResourceLocation> set = isRare ? rareSet : normalSet;
        int choice = Math.abs((int)(id.getMostSignificantBits() % set.size()));
        return set.get(choice);
    }

    private ResourceLocation createResource(MobType type, String texture) {
        return new ResourceLocation(Charm.MOD_ID, PREFIX + type.getString() + "/" + texture + ".png");
    }

    public enum MobType implements ICharmEnum { WOLF, COW, PIG, CHICKEN, SQUID, SHEEP, SNOW_GOLEM }
}

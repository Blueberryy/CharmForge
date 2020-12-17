package svenhjol.charm.base.handler;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import svenhjol.charm.Charm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@SuppressWarnings({"UnusedReturnValue", "unchecked", "rawtypes"})
@Mod.EventBusSubscriber(bus = MOD)
public class RegistryHandler {
    private static final Map<String, Map<IForgeRegistry<?>, List<Supplier<IForgeRegistryEntry<?>>>>> REGISTRY = new HashMap<>();

    @SubscribeEvent
    public static void onRegister(RegistryEvent.Register<?> event) {
        IForgeRegistry registry = event.getRegistry();

        REGISTRY.forEach((modId, registrations) -> {
            if (registrations.containsKey(registry)) {
                registrations.get(registry).forEach(supplier -> {
                    IForgeRegistryEntry<?> entry = supplier.get();
                    if (entry != null) {
                        Charm.LOG.debug("Registering to " + registry.getRegistryName() + " - " + entry.getRegistryName());
                        registry.register(entry);
                    }
                });
            } else {
                Charm.LOG.debug("Owner registry has no event data, skipping registry event " + event.getName());
            }
        });
    }

    public static Block block(ResourceLocation resId, Block block) {
        return register(ForgeRegistries.BLOCKS, resId, block);
    }

    public static <T extends TileEntity> TileEntityType<T> tileEntity(ResourceLocation resId, Supplier<T> supplier, Block... blocks) {
        TileEntityType<T> build = TileEntityType.Builder.create(supplier, blocks).build(null);
        register(ForgeRegistries.TILE_ENTITIES, resId, build);
        return build;
    }

    public static StructureFeature<?, ?> configuredFeature(ResourceLocation resId, StructureFeature<?, ?> configuredFeature) {
        WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, resId, configuredFeature);
        return configuredFeature;
    }

    public static <T extends Entity> EntityType<T> entity(ResourceLocation resId, EntityType<T> entityType) {
        register(ForgeRegistries.ENTITIES, resId, entityType);
        return entityType;
    }

    public static Enchantment enchantment(ResourceLocation resId, Enchantment enchantment) {
        return register(ForgeRegistries.ENCHANTMENTS, resId, enchantment);
    }

    public static Item item(ResourceLocation resId, Item item) {
        return register(ForgeRegistries.ITEMS, resId, item);
    }

    public static LootFunctionType lootFunctionType(ResourceLocation resId, LootFunctionType lootFunctionType) {
        return Registry.register(Registry.LOOT_FUNCTION_TYPE, resId, lootFunctionType);
    }

    public static PointOfInterestType pointOfInterestType(ResourceLocation resId, PointOfInterestType poit) {
        return register(ForgeRegistries.POI_TYPES, resId, poit);
    }

    public static <T extends IRecipe<?>> IRecipeType<T> recipeType(String recipeId) {
        return IRecipeType.register(recipeId);
    }

    public static <S extends IRecipeSerializer<T>, T extends IRecipe<?>> S recipeSerializer(String recipeId, S serializer) {
        register(ForgeRegistries.RECIPE_SERIALIZERS, new ResourceLocation(recipeId), serializer);
        return serializer;
    }

    public static <T extends Container> ContainerType<T> container(ResourceLocation resId, ContainerType.IFactory<T> factory) {
        ContainerType<T> container = new ContainerType<>(factory);
        register(ForgeRegistries.CONTAINERS, resId, container);
        return container;
    }

    public static SoundEvent sound(ResourceLocation resId, SoundEvent sound) {
        return register(ForgeRegistries.SOUND_EVENTS, resId, sound);
    }

    public static IStructurePieceType structurePiece(ResourceLocation resId, IStructurePieceType structurePieceType) {
        return Registry.register(Registry.STRUCTURE_PIECE, resId, structurePieceType);
    }

    public static VillagerProfession villagerProfession(ResourceLocation resId, VillagerProfession profession) {
        return register(ForgeRegistries.PROFESSIONS, resId, profession);
    }

    public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> type, Supplier<IForgeRegistryEntry<?>> supplier) {
        String modId = getOwnerContext();

        REGISTRY.putIfAbsent(modId, new HashMap<>());
        Map<IForgeRegistry<?>, List<Supplier<IForgeRegistryEntry<?>>>> modRegistry = REGISTRY.get(modId);

        modRegistry.putIfAbsent(type, new ArrayList<>());
        modRegistry.get(type).add(supplier);
    }

    public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> type, ResourceLocation id, T entry) {
        String modId = getOwnerContext();
        entry.setRegistryName(id);

        REGISTRY.putIfAbsent(modId, new HashMap<>());
        Map<IForgeRegistry<?>, List<Supplier<IForgeRegistryEntry<?>>>> modRegistry = REGISTRY.get(modId);

        modRegistry.putIfAbsent(type, new ArrayList<>());
        modRegistry.get(type).add(() -> entry);
        return entry;
    }

    private static String getOwnerContext() {
        return ModLoadingContext.get().getActiveNamespace();
    }
}

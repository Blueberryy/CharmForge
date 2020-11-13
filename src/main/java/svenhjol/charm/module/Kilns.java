package svenhjol.charm.module;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.DecorationHelper;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.KilnBlock;
import svenhjol.charm.client.KilnsClient;
import svenhjol.charm.container.KilnScreenHandler;
import svenhjol.charm.recipe.FiringRecipe;
import svenhjol.charm.tileentity.KilnTileEntity;

@Module(mod = Charm.MOD_ID, client = KilnsClient.class, description = "A functional block that speeds up cooking of clay, bricks and terracotta.")
public class Kilns extends CharmModule {
    public static ResourceLocation RECIPE_ID = new ResourceLocation("firing");
    public static ResourceLocation BLOCK_ID = new ResourceLocation(Charm.MOD_ID, "kiln");
    public static KilnBlock KILN;
    public static TileEntityType<KilnTileEntity> BLOCK_ENTITY;
    public static IRecipeType<FiringRecipe> RECIPE_TYPE;
    public static CookingRecipeSerializer<FiringRecipe> RECIPE_SERIALIZER;
    public static ContainerType<KilnScreenHandler> CONTAINER;

    @Override
    public void register() {
        KILN = new KilnBlock(this);
        RECIPE_TYPE = RegistryHandler.recipeType(RECIPE_ID.toString());
        RECIPE_SERIALIZER = RegistryHandler.recipeSerializer(RECIPE_ID.toString(), new CookingRecipeSerializer<>(FiringRecipe::new, 100));
        BLOCK_ENTITY = RegistryHandler.tileEntity(BLOCK_ID, KilnTileEntity::new, KILN);
        CONTAINER = RegistryHandler.container(BLOCK_ID, KilnScreenHandler::new);
    }

    @Override
    public void init() {
        DecorationHelper.DECORATION_BLOCKS.add(KILN);
        DecorationHelper.STATE_CALLBACK.put(KILN, facing -> KILN.getDefaultState().with(KilnBlock.FACING, facing));
    }
}

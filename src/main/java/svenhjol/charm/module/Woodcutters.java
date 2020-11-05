package svenhjol.charm.module;

import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ClientRegistryHandler;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.WoodcutterBlock;
import svenhjol.charm.gui.WoodcutterScreen;
import svenhjol.charm.recipe.WoodcuttingRecipe;
import svenhjol.charm.screenhandler.WoodcutterContainer;

@Module(mod = Charm.MOD_ID, description = "A functional block that adds more efficient recipes for crafting wooden stairs and slabs.")
public class Woodcutters extends CharmModule {
    public static ResourceLocation RECIPE_ID = new ResourceLocation("woodcutting");
    public static ResourceLocation BLOCK_ID = new ResourceLocation(Charm.MOD_ID, "woodcutter");
    public static WoodcutterBlock WOODCUTTER;
    public static ScreenHandlerType<WoodcutterContainer> SCREEN_HANDLER;
    public static IRecipeType<WoodcuttingRecipe> RECIPE_TYPE;
    public static RecipeSerializer<WoodcuttingRecipe> RECIPE_SERIALIZER;

    @Override
    public void register() {
        WOODCUTTER = new WoodcutterBlock(this);
        RECIPE_TYPE = RegistryHandler.recipeType(RECIPE_ID.toString());
        RECIPE_SERIALIZER = RegistryHandler.recipeSerializer(RECIPE_ID.toString(), new WoodcuttingRecipe.Serializer<>(WoodcuttingRecipe::new));
        SCREEN_HANDLER = RegistryHandler.screenHandler(BLOCK_ID, WoodcutterContainer::new);
    }

    @Override
    public void clientRegister() {
        ClientRegistryHandler.setRenderLayer(WOODCUTTER, RenderLayer.getCutout());
    }

    @Override
    public void clientInit() {
        ClientRegistryHandler.screenHandler(SCREEN_HANDLER, WoodcutterScreen::new);
    }
}

package svenhjol.charm.base.handler;

import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;

@SuppressWarnings("UnusedReturnValue")
public class ClientRegistryHandler {
    public static <H extends ScreenHandler, S extends Screen & ScreenHandlerProvider<H>> void screenHandler(ScreenHandlerType<? extends H> screenHandler, ScreenRegistry.Factory<H, S> screen) {
        ScreenRegistry.register(screenHandler, screen);
    }

    public static void setRenderLayer(Block block, RenderType renderLayer) {
        RenderTypeLookup.setRenderLayer(block, renderLayer);
    }
}

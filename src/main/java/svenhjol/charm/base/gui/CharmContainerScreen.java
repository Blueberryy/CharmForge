package svenhjol.charm.base.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import svenhjol.charm.base.CharmResources;

public class CharmContainerScreen<T extends Container> extends AbstractCharmContainerScreen<T> {

    public CharmContainerScreen(int rows, T handler, PlayerInventory inventory, ITextComponent title) {
        super(handler, inventory, title, getTextureFromRows(rows));
        this.xSize = 175;
        this.ySize = 111 + 20 * rows;
    }

    private static ResourceLocation getTextureFromRows(int rows) {
        switch (rows) {
            case 1:
                return CharmResources.GUI_9_TEXTURE;
            case 2:
                return CharmResources.GUI_18_TEXTURE;
            default:
                throw new IllegalArgumentException("Unsupported row count " + rows);
        }
    }

    public static <T extends Container> ScreenManager.IScreenFactory<T, CharmContainerScreen<T>> createFactory(int rows) {
        return (handler, inventory, title) -> new CharmContainerScreen<>(rows, handler, inventory, title);
    }

}

package svenhjol.charm.base.helper;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import svenhjol.charm.mixin.accessor.ContainerScreenAccessor;

public class ScreenHelper {
    public static int getX(ContainerScreen<?> screen) {
        return ((ContainerScreenAccessor)screen).getGuiLeft();
    }

    public static int getY(ContainerScreen<?> screen) {
        return ((ContainerScreenAccessor)screen).getGuiTop();
    }
}

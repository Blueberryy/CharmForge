package svenhjol.charm.base.helper;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import svenhjol.charm.mixin.accessor.HandledScreenAccessor;

public class ScreenHelper {
    public static int getX(ContainerScreen<?> screen) {
        return ((HandledScreenAccessor)screen).getX();
    }

    public static int getY(ContainerScreen<?> screen) {
        return ((HandledScreenAccessor)screen).getY();
    }
}

package svenhjol.charm.base.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;

public class ClientHelper {
    public static void openInventory() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        mc.displayGuiScreen(new InventoryScreen(mc.player));
    }
}

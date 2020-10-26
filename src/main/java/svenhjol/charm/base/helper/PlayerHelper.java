package svenhjol.charm.base.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerHelper {
    /**
     * Tries to add item stack to player, drops if not possible.
     *
     * @param player The player
     * @param stack  The stack to add/drop
     * @return True if able to add to player inv, false if dropped
     */
    public static boolean addOrDropStack(PlayerEntity player, ItemStack stack) {
        if (!player.inventory.addItemStackToInventory(stack)) {
            player.dropItem(stack, true);
            return false;
        }
        return true;
    }

    public static void openInventory() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        mc.displayGuiScreen(new InventoryScreen(mc.player));
    }
}

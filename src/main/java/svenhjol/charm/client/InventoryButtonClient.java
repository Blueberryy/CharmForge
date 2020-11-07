package svenhjol.charm.client;

import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.helper.ScreenHelper;
import svenhjol.charm.module.PortableCrafting;
import svenhjol.charm.module.PortableEnderChest;

public class InventoryButtonClient {
    public ImageButton recipeButton;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof InventoryScreen) || event.getGui() instanceof CreativeScreen)
            return;

        // get recipe button from the widgetlist
        if (!event.getWidgetList().isEmpty() && event.getWidgetList().get(0) instanceof ImageButton)
            this.recipeButton = (ImageButton)event.getWidgetList().get(0);

        redrawButtons((InventoryScreen)event.getGui());
    }

    @SubscribeEvent
    public void onDrawForeground(GuiContainerEvent.DrawForeground event) {
        if (!(event.getGuiContainer() instanceof InventoryScreen) || event.getGuiContainer() instanceof CreativeScreen)
            return;

        redrawButtons((InventoryScreen)event.getGuiContainer());
    }

    private void redrawButtons(InventoryScreen screen) {
        int y = screen.height / 2 - 22;
        int left = ScreenHelper.getX(screen);

        if (PortableCrafting.client != null && PortableCrafting.client.isButtonVisible()) {
            if (PortableEnderChest.client.isButtonVisible()) {
                // recipe, crafting and chest buttons
                if (this.recipeButton != null)
                    this.recipeButton.visible = false;
                PortableCrafting.client.craftingButton.setPosition(left + 104, y);
                PortableEnderChest.client.chestButton.setPosition(left + 130, y);

            } else {
                // just the recipe and crafting buttons
                if (this.recipeButton != null)
                    this.recipeButton.visible = true;
                PortableCrafting.client.craftingButton.setPosition(left + 130, y);

            }
        } else if (PortableEnderChest.client != null && PortableEnderChest.client.isButtonVisible()) {
            // just the recipe and chest buttons
            if (this.recipeButton != null)
                this.recipeButton.visible = true;
            PortableEnderChest.client.chestButton.setPosition(left + 130, y);
        }
    }
}

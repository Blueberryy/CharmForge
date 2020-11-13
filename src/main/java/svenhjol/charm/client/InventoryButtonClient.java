package svenhjol.charm.client;

import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ClientHandler;
import svenhjol.charm.base.helper.ScreenHelper;

public class InventoryButtonClient extends CharmClientModule {
    public ImageButton recipeButton;
    public PortableCraftingClient portableCraftingClient;
    public PortableEnderChestClient portableEnderChestClient;

    public InventoryButtonClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        portableCraftingClient = (PortableCraftingClient)ClientHandler.getModule("charm:portable_crafting");
        portableEnderChestClient = (PortableEnderChestClient)ClientHandler.getModule("charm:portable_ender_chest");
    }

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

        if (portableCraftingClient != null && portableCraftingClient.isButtonVisible()) {
            if (portableEnderChestClient.isButtonVisible()) {
                // recipe, crafting and chest buttons
                if (this.recipeButton != null)
                    this.recipeButton.visible = false;
                portableCraftingClient.craftingButton.setPosition(left + 104, y);
                portableEnderChestClient.chestButton.setPosition(left + 130, y);

            } else {
                // just the recipe and crafting buttons
                if (this.recipeButton != null)
                    this.recipeButton.visible = true;
                portableCraftingClient.craftingButton.setPosition(left + 130, y);

            }
        } else if (portableEnderChestClient != null && portableEnderChestClient.isButtonVisible()) {
            // just the recipe and chest buttons
            if (this.recipeButton != null)
                this.recipeButton.visible = true;
            portableEnderChestClient.chestButton.setPosition(left + 130, y);
        }
    }
}

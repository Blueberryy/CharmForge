package svenhjol.charm.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.message.ServerOpenCrafting;
import svenhjol.charm.module.PortableCrafting;

public class PortableCraftingClient extends CharmClientModule {
    public ImageButton craftingButton;
    public static KeyBinding keyBinding;

    public PortableCraftingClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        if (PortableCrafting.enableKeybind) {
            keyBinding = new KeyBinding("key.charm.openCraftingTable", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.inventory");
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onKeyboardKeyPressed(InputEvent.KeyInputEvent event) {
        if (keyBinding != null && keyBinding.matchesKey(event.getKey(), event.getScanCode()))
            triggerOpenCraftingTable();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (!(event.getGui() instanceof InventoryScreen) || event.getGui() instanceof CreativeScreen)
            return;

        InventoryScreen screen = (InventoryScreen)event.getGui();

        this.craftingButton = new ImageButton(screen.getGuiLeft() + 130, screen.height / 2 - 22, 20, 18, 0, 0, 19, CharmResources.INVENTORY_BUTTONS, click -> {
            triggerOpenCraftingTable();
        });

        craftingButton.visible = this.hasCrafting(mc.player);
        event.addWidget(this.craftingButton);
    }

    @SubscribeEvent
    public void onDrawForeground(GuiContainerEvent.DrawForeground event) {
        if (!(event.getGuiContainer() instanceof InventoryScreen))
            return;

        if (craftingButton == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        if (mc.player.world.getGameTime() % 5 == 0)
            craftingButton.visible = this.hasCrafting(mc.player);
    }

    private boolean hasCrafting(PlayerEntity player) {
        return player.inventory.hasItemStack(new ItemStack(Blocks.CRAFTING_TABLE));
    }

    private void triggerOpenCraftingTable() {
        Charm.PACKET_HANDLER.sendToServer(new ServerOpenCrafting());
    }

    public boolean isButtonVisible() {
        return this.craftingButton != null && this.craftingButton.visible;
    }
}

package svenhjol.charm.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.message.ServerOpenEnderChest;
import svenhjol.charm.module.PortableEnderChest;

public class PortableEnderChestClient extends CharmClientModule {
    public ImageButton chestButton;
    public static KeyBinding keyBinding;

    public PortableEnderChestClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        if (PortableEnderChest.enableKeybind) {
            keyBinding = new KeyBinding("key.charm.openEnderChest", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.inventory");
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }

    @SubscribeEvent
    public void onKeyboardKeyPressed(InputEvent.KeyInputEvent event) {
        if (keyBinding != null && keyBinding.matchesKey(event.getKey(), event.getScanCode()))
            triggerOpenChest();
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (!(event.getGui() instanceof InventoryScreen))
            return;

        InventoryScreen screen = (InventoryScreen) event.getGui();

        this.chestButton = new ImageButton(screen.getGuiLeft() + 130, screen.height / 2 - 22, 20, 18, 20, 0, 19, CharmResources.INVENTORY_BUTTONS, click -> {
            triggerOpenChest();
        });

        chestButton.visible = this.hasChest(mc.player);
        event.addWidget(this.chestButton);
    }

    @SubscribeEvent
    public void onDrawForeground(GuiContainerEvent.DrawForeground event) {
        if (!(event.getGuiContainer() instanceof InventoryScreen))
            return;

        if (chestButton == null)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        if (mc.player.world.getGameTime() % 5 == 0)
            chestButton.visible = this.hasChest(mc.player);
    }

    private boolean hasChest(PlayerEntity player) {
        return player.inventory.hasItemStack(new ItemStack(Blocks.ENDER_CHEST));
    }

    private void triggerOpenChest() {
        Charm.PACKET_HANDLER.sendToServer(new ServerOpenEnderChest());
    }

    public boolean isButtonVisible() {
        return this.chestButton != null && this.chestButton.visible;
    }
}

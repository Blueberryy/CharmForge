package svenhjol.charm.client;

import io.netty.buffer.Unpooled;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.base.helper.ScreenHelper;
import svenhjol.charm.module.PortableCrafting;

import java.util.List;
import java.util.function.Consumer;

public class PortableCraftingClient {
    public TexturedButtonWidget craftingButton;
    public static KeyBinding keyBinding;

    public PortableCraftingClient(CharmModule module) {
        if (PortableCrafting.enableKeybind) {
            keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.charm.openCraftingTable",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.categories.inventory"
            ));

            ClientTickEvents.END_WORLD_TICK.register(client -> {
                while (keyBinding.wasPressed()) {
                    triggerOpenCraftingTable();
                }
            });
        }
    }

    private void handleGuiSetup(Minecraft client, int width, int height, List<AbstractButtonWidget> buttons, Consumer<AbstractButtonWidget> addButton) {
        if (client.player == null)
            return;

        if (!(client.currentScreen instanceof InventoryScreen))
            return;

        InventoryScreen screen = (InventoryScreen)client.currentScreen;
        int guiLeft = ScreenHelper.getX(screen);

        this.craftingButton = new TexturedButtonWidget(guiLeft + 130, height / 2 - 22, 20, 18, 0, 0, 19, CharmResources.INVENTORY_BUTTONS, click -> {
            triggerOpenCraftingTable();
        });

        this.craftingButton.visible = hasCrafting(client.player);
        addButton.accept(this.craftingButton);
    }

    private void handleRenderGui(Minecraft client, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!(client.currentScreen instanceof InventoryScreen)
            || this.craftingButton == null
            || client.player == null
        ) {
            return;
        }

        if (client.player.world.getTime() % 5 == 0)
            this.craftingButton.visible = hasCrafting(client.player);
    }

    private boolean hasCrafting(PlayerEntity player) {
        return player.inventory.contains(new ItemStack(Blocks.CRAFTING_TABLE));
    }

    private void triggerOpenCraftingTable() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(PortableCrafting.MSG_SERVER_OPEN_CRAFTING, new PacketByteBuf(Unpooled.buffer()));
    }

    public boolean isButtonVisible() {
        return this.craftingButton != null && this.craftingButton.visible;
    }
}

package svenhjol.charm.client;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.base.helper.ScreenHelper;
import svenhjol.charm.gui.BookcaseScreen;
import svenhjol.charm.gui.CrateScreen;
import svenhjol.charm.mixin.accessor.SlotAccessor;
import svenhjol.charm.module.InventoryTidying;

import java.util.*;
import java.util.function.Consumer;

import static svenhjol.charm.handler.InventoryTidyingHandler.BE;
import static svenhjol.charm.handler.InventoryTidyingHandler.PLAYER;

public class InventoryTidyingClient {
    private final CharmModule module;

    public static final int LEFT = 159;
    public static final int TOP = 12;
    public static final List<TexturedButtonWidget> sortingButtons = new ArrayList<>();

    public final List<Class<? extends Screen>> TileEntityScreens = new ArrayList<>();
    public final List<Class<? extends Screen>> blacklistScreens = new ArrayList<>();

    public final Map<Class<? extends Screen>, Map<Integer, Integer>> screenTweaks = new HashMap<>();

    public InventoryTidyingClient(CharmModule module) {
        this.module = module;

        if (!module.enabled)
            return;

        screenTweaks.put(MerchantScreen.class, new HashMap<Integer, Integer>() {{ put(100, 0); }});
        screenTweaks.put(InventoryScreen.class, new HashMap<Integer, Integer>() {{ put(0, 76); }});

        TileEntityScreens.addAll(Arrays.asList(
            GenericContainerScreen.class,
            HopperScreen.class,
            ShulkerBoxScreen.class,
            CrateScreen.class,
            BookcaseScreen.class,
            Generic3x3ContainerScreen.class
        ));

        blacklistScreens.addAll(Arrays.asList(
            CreativeInventoryScreen.class,
            BeaconScreen.class
        ));
    }

    private void handleGuiSetup(Minecraft client, int width, int height, List<AbstractButtonWidget> buttons, Consumer<AbstractButtonWidget> addButton) {
        if (client.player == null)
            return;

        if (!(client.currentScreen instanceof HandledScreen))
            return;

        if (blacklistScreens.contains(client.currentScreen.getClass()))
            return;

        sortingButtons.clear();

        HandledScreen<?> screen = (HandledScreen<?>)client.currentScreen;
        Class<? extends HandledScreen> clazz = screen.getClass();
        ScreenHandler screenHandler = screen.getScreenHandler();

        int x = ScreenHelper.getX(screen) + LEFT;
        int y = ScreenHelper.getY(screen) - TOP;

        if (screenTweaks.containsKey(clazz)) {
            Map<Integer, Integer> m = screenTweaks.get(clazz);
            for (Map.Entry<Integer, Integer> e : m.entrySet()) {
                x += e.getKey();
                y += e.getValue();
            }
        }

        List<Slot> slots = screenHandler.slots;
        for (Slot slot : slots) {
            if (blockEntityScreens.contains(screen.getClass()) && ((SlotAccessor)slot).getIndex() == 0) {
                this.addSortingButton(screen, x, y + slot.y, click -> sendSortMessage(BE));
            }

            if (slot.inventory == client.player.inventory) {
                this.addSortingButton(screen, x, y + slot.y, click -> sendSortMessage(PLAYER));
                break;
            }
        }

        sortingButtons.forEach(addButton);
    }

    private void handleRenderGui(Minecraft client, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (client.currentScreen instanceof InventoryScreen
            && !blacklistScreens.contains(client.currentScreen.getClass())
        ) {
            // handles the recipe being open/closed
            InventoryScreen screen = (InventoryScreen)client.currentScreen;
            int x = ScreenHelper.getX(screen);
            sortingButtons.forEach(button -> button.setPos(x + LEFT, button.y));
        }
    }

    private void addSortingButton(Screen screen, int x, int y, ButtonWidget.PressAction onPress) {
        sortingButtons.add(new TexturedButtonWidget(x, y, 10, 10, 40, 0, 10, CharmResources.INVENTORY_BUTTONS, onPress));
    }

    private void sendSortMessage(int type) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeInt(type);
        ClientSidePacketRegistry.INSTANCE.sendToServer(InventoryTidying.MSG_SERVER_TIDY_INVENTORY, data);
    }
}

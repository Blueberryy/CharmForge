package svenhjol.charm.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.*;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.gui.BookcaseScreen;
import svenhjol.charm.gui.CrateScreen;
import svenhjol.charm.message.ServerSortInventory;

import java.util.*;

import static svenhjol.charm.handler.InventoryTidyingHandler.BE;
import static svenhjol.charm.handler.InventoryTidyingHandler.PLAYER;

public class InventoryTidyingClient extends CharmClientModule {
    public static final int LEFT = 159;
    public static final int TOP = 12;
    public static final List<ImageButton> sortingButtons = new ArrayList<>();

    public final List<Class<? extends Screen>> tileScreens = new ArrayList<>();
    public final List<Class<? extends Screen>> blacklistScreens = new ArrayList<>();

    public final Map<Class<? extends Screen>, Map<Integer, Integer>> screenTweaks = new HashMap<>();

    public InventoryTidyingClient(CharmModule module) {
       super(module);
    }

    @Override
    public void register() {
        if (!module.enabled)
            return;

        screenTweaks.put(MerchantScreen.class, new HashMap<Integer, Integer>() {{ put(100, 0); }});
        screenTweaks.put(InventoryScreen.class, new HashMap<Integer, Integer>() {{ put(0, 76); }});

        tileScreens.addAll(Arrays.asList(
            ChestScreen.class, // yarn: GenericContainerScreen
            HopperScreen.class,
            ShulkerBoxScreen.class,
            CrateScreen.class,
            BookcaseScreen.class,
            DispenserScreen.class // yarn: Generic3x3ContainerScreen
        ));

        blacklistScreens.addAll(Arrays.asList(
            CreativeScreen.class,
            BeaconScreen.class
        ));
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!module.enabled) return;

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null)
            return;

        if (!(event.getGui() instanceof ContainerScreen))
            return;

        if (blacklistScreens.contains(event.getGui().getClass()))
            return;

        sortingButtons.clear();

        ContainerScreen<?> screen = (ContainerScreen<?>) event.getGui();
        Class<? extends ContainerScreen> clazz = screen.getClass();
        Container container = screen.getContainer();

        int x = screen.getGuiLeft() + LEFT;
        int y = screen.getGuiTop() - TOP;

        if (screenTweaks.containsKey(clazz)) {
            Map<Integer, Integer> m = screenTweaks.get(clazz);
            for (Map.Entry<Integer, Integer> e : m.entrySet()) {
                x += e.getKey();
                y += e.getValue();
            }
        }

        List<Slot> slots = container.inventorySlots;
        for (Slot slot : slots) {
            if (tileScreens.contains(screen.getClass()) && slot.getSlotIndex() == 0) {
                this.addSortingButton(screen, x, y + slot.yPos, click -> sendSortMessage(BE));
            }

            if (slot.inventory == Minecraft.getInstance().player.inventory) {
                this.addSortingButton(screen, x, y + slot.yPos, click -> sendSortMessage(PLAYER));
                break;
            }
        }

        sortingButtons.forEach(event::addWidget);
    }

    @SubscribeEvent
    public void onDrawForeground(GuiContainerEvent.DrawForeground event) {
        // redraw all buttons on inventory to handle recipe open/close
        if (event.getGuiContainer() instanceof InventoryScreen
            && !blacklistScreens.contains(event.getGuiContainer().getClass())
        ) {
            InventoryScreen screen = (InventoryScreen)event.getGuiContainer();
            sortingButtons.forEach(button -> button.setPosition(screen.getGuiLeft() + LEFT, button.y));
        }
    }

    private void addSortingButton(Screen screen, int x, int y, Button.IPressable onPress) {
        sortingButtons.add(new ImageButton(x, y, 10, 10, 40, 0, 10, CharmResources.INVENTORY_BUTTONS, onPress));
    }

    private void sendSortMessage(int type) {
        Charm.PACKET_HANDLER.sendToServer(new ServerSortInventory(type));
    }
}

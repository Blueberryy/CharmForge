package svenhjol.charm.module;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.InventoryTidyingClient;
import svenhjol.charm.handler.InventoryTidyingHandler;

import java.util.List;

import static svenhjol.charm.handler.InventoryTidyingHandler.BE;
import static svenhjol.charm.handler.InventoryTidyingHandler.PLAYER;

@Module(mod = Charm.MOD_ID, description = "Button to automatically tidy inventories.")
public class InventoryTidying extends CharmModule {
    public static InventoryTidyingClient client;
    public static final ResourceLocation MSG_SERVER_TIDY_INVENTORY = new ResourceLocation(Charm.MOD_ID, "server_tidy_inventory");

    @Override
    public void init() {
        // listen for network requests to run the server callback
        ServerSidePacketRegistry.INSTANCE.register(MSG_SERVER_TIDY_INVENTORY, (context, data) -> {
            int type = data.readInt();

            context.getTaskQueue().execute(() -> {
                ServerPlayerEntity player = (ServerPlayerEntity)context.getPlayer();
                if (player == null)
                    return;

                InventoryTidying.serverCallback(player, type);
            });
        });

        InventoryTidyingHandler.init();
    }

    @Override
    public void clientInit() {
        client = new InventoryTidyingClient(this);
    }

    public static void serverCallback(ServerPlayerEntity player, int type) {
        ContainerScreen<?> useContainer;

        if (player.isSpectator())
            return;

        if (type == PLAYER && player.playerScreenHandler != null) {
            useContainer = player.playerScreenHandler;
        } else if (type == BE && player.currentScreenHandler != null) {
            useContainer = player.currentScreenHandler;
        } else {
            return;
        }

        List<Slot> slots = useContainer.getContainer().inventorySlots;
        for (Slot slot : slots) {
            IInventory inventory = slot.inventory;

            if (type == PLAYER && slot.inventory == player.inventory) {
                InventoryTidyingHandler.sort(player.inventory, 9, 36);
                break;
            } else if (type == BE) {
                InventoryTidyingHandler.sort(inventory, 0, inventory.getSizeInventory());
                break;
            }
        }
    }
}

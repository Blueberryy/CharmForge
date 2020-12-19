package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.InventoryTidyingClient;
import svenhjol.charm.handler.InventoryTidyingHandler;

import java.util.List;

import static svenhjol.charm.handler.InventoryTidyingHandler.BE;
import static svenhjol.charm.handler.InventoryTidyingHandler.PLAYER;

@Module(mod = Charm.MOD_ID, client = InventoryTidyingClient.class, description = "Button to automatically tidy inventories.", hasSubscriptions = true)
public class InventoryTidying extends CharmModule {
    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public boolean depends() {
        return !ModuleHandler.enabled("quark:management.module.inventory_sorting_module") || override;
    }

    @Override
    public void init() {
        InventoryTidyingHandler.init();
    }

    public static void serverCallback(ServerPlayerEntity player, int type) {
        Container useContainer;

        if (player.isSpectator())
            return;

        if (type == PLAYER && player.container != null) {
            useContainer = player.container;
        } else if (type == BE && player.openContainer != null) {
            useContainer = player.openContainer;
        } else {
            return;
        }

        List<Slot> slots = useContainer.inventorySlots;
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

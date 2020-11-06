package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ContainerType;
import svenhjol.charm.module.Core;

public class PortableEnderChestScreenHandler extends ChestContainer {
    public PortableEnderChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ContainerType.GENERIC_9X3, syncId, playerInventory, inventory, 3);
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        super.onContainerClosed(player);

        if (!player.world.isRemote && Core.inventoryButtonReturn) {
            // TODO: handle send packet
//            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, Core.MSG_SERVER_OPEN_INVENTORY, new PacketByteBuf(Unpooled.buffer()));
        }
    }
}

package svenhjol.charm.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.util.IWorldPosCallable;
import svenhjol.charm.module.Core;

public class PortableCraftingContainer extends WorkbenchContainer {
    public PortableCraftingContainer(int syncId, PlayerInventory playerInventory, IWorldPosCallable context) {
        super(syncId, playerInventory, context);
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

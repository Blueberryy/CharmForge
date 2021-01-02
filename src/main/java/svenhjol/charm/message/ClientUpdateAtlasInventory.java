package svenhjol.charm.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.client.AtlasClient;

import java.util.function.Supplier;

public class ClientUpdateAtlasInventory implements ICharmMessage {
    public final int atlasSlot;

    public ClientUpdateAtlasInventory(int atlasSlot) {
        this.atlasSlot = atlasSlot;
    }

    public static void encode(ClientUpdateAtlasInventory msg, PacketBuffer buf) {
        buf.writeVarInt(msg.atlasSlot);
    }

    public static ClientUpdateAtlasInventory decode(PacketBuffer buf) {
        return new ClientUpdateAtlasInventory(buf.readVarInt());
    }

    public static class Handler {
        public static void handle(final ClientUpdateAtlasInventory msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                AtlasClient.updateInventory(msg.atlasSlot);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

package svenhjol.charm.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.client.BatBucketsClient;

import java.util.function.Supplier;

public class ClientSetGlowingEntities implements ICharmMessage {
    private final double range;
    private final int ticks;

    public ClientSetGlowingEntities(double range, int ticks) {
        this.range = range;
        this.ticks = ticks;
    }

    public static void encode(ClientSetGlowingEntities msg, PacketBuffer buf) {
        buf.writeDouble(msg.range);
        buf.writeInt(msg.ticks);
    }

    public static ClientSetGlowingEntities decode(PacketBuffer buf) {
        return new ClientSetGlowingEntities(buf.readDouble(), buf.readInt());
    }

    public static class Handler {
        public static void handle(final ClientSetGlowingEntities msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                BatBucketsClient.range = msg.range;
                BatBucketsClient.ticks = msg.ticks;
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

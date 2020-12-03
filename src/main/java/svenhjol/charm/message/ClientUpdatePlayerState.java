package svenhjol.charm.message;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.client.PlayerStateClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.function.Supplier;

public class ClientUpdatePlayerState implements ICharmMessage {
    private final CompoundNBT data;

    public ClientUpdatePlayerState(CompoundNBT data) {
        this.data = data;
    }

    public static void encode(ClientUpdatePlayerState msg, PacketBuffer buf) {
        String serialized = "";

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(msg.data, out);
            serialized = Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            Charm.LOG.warn("Failed to compress player state");
        }
        buf.writeString(serialized);
    }

    public static ClientUpdatePlayerState decode(PacketBuffer buf) {
        CompoundNBT data = new CompoundNBT();

        try {
            final byte[] byteData = Base64.getDecoder().decode(buf.readString());
            data = CompressedStreamTools.readCompressed(new ByteArrayInputStream(byteData));
        } catch (Exception e) {
            Charm.LOG.warn("Failed to decompress player state");
        }

        return new ClientUpdatePlayerState(data);
    }

    public static class Handler {
        public static void handle(final ClientUpdatePlayerState msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (PlayerStateClient.INSTANCE != null)
                    PlayerStateClient.INSTANCE.clientCallback(msg.data);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

package svenhjol.charm.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.module.Atlas;

import java.util.function.Supplier;

/**
 * @author Lukas
 * @since 29.12.2020
 */
public class ServerTransferStackFromAtlas implements ICharmMessage {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SHIFT = 1;
    public final int atlasSlot;
    public final int mapSlot;
    public final int mode;

    public ServerTransferStackFromAtlas(int atlasSlot, int mapSlot, int mode) {
        this.atlasSlot = atlasSlot;
        this.mapSlot = mapSlot;
        this.mode = mode;
    }

    public static void encode(ServerTransferStackFromAtlas msg, PacketBuffer buf) {
        buf.writeVarInt(msg.atlasSlot);
        buf.writeInt(msg.mapSlot);
        buf.writeVarInt(msg.mode);
    }

    public static ServerTransferStackFromAtlas decode(PacketBuffer buf) {
        return new ServerTransferStackFromAtlas(buf.readVarInt(), buf.readInt(), buf.readVarInt());
    }

    public static class Handler {
        public static void handle(final ServerTransferStackFromAtlas msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                NetworkEvent.Context context = ctx.get();
                ServerPlayerEntity player = context.getSender();

                if (player == null)
                    return;

                Atlas.serverCallback(player, msg);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

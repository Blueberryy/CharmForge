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
public class ServerAtlasTransfer implements ICharmMessage {
    public enum MoveMode {
        TO_HAND, TO_INVENTORY, FROM_HAND, FROM_INVENTORY
    }
    public final int atlasSlot;
    public final int mapX;
    public final int mapZ;
    public final MoveMode mode;

    public ServerAtlasTransfer(int atlasSlot, int mapX, int mapZ, MoveMode mode) {
        this.atlasSlot = atlasSlot;
        this.mapX = mapX;
        this.mapZ = mapZ;
        this.mode = mode;
    }

    public static void encode(ServerAtlasTransfer msg, PacketBuffer buf) {
        buf.writeVarInt(msg.atlasSlot);
        buf.writeInt(msg.mapX);
        buf.writeInt(msg.mapZ);
        buf.writeEnumValue(msg.mode);
    }

    public static ServerAtlasTransfer decode(PacketBuffer buf) {
        return new ServerAtlasTransfer(buf.readVarInt(), buf.readInt(), buf.readInt(), buf.readEnumValue(MoveMode.class));
    }

    public static class Handler {
        public static void handle(final ServerAtlasTransfer msg, Supplier<NetworkEvent.Context> ctx) {
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

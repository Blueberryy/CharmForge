package svenhjol.charm.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.module.InventoryTidying;

import java.util.function.Supplier;

public class ServerSortInventory implements ICharmMessage {
    public static final int PLAYER = 0;
    public static final int TILE = 1;

    private final int type;

    public ServerSortInventory(int type) {
        this.type = type;
    }

    public static void encode(ServerSortInventory msg, PacketBuffer buf) {
        buf.writeInt(msg.type);
    }

    public static ServerSortInventory decode(PacketBuffer buf) {
        return new ServerSortInventory(
            buf.readInt()
        );
    }

    public static class Handler {
        public static void handle(final ServerSortInventory msg, Supplier<Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Context context = ctx.get();
                ServerPlayerEntity player = context.getSender();

                if (player == null)
                    return;

                InventoryTidying.serverCallback(player, msg.type);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

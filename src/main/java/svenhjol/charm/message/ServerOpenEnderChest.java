package svenhjol.charm.message;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.module.PortableEnderChest;

import java.util.function.Supplier;

@SuppressWarnings("EmptyMethod")
public class ServerOpenEnderChest implements ICharmMessage {
    public static void encode(ServerOpenEnderChest msg, PacketBuffer buf) { }

    public static ServerOpenEnderChest decode(PacketBuffer buf) {
        return new ServerOpenEnderChest();
    }

    public static class Handler {
        public static void handle(final ServerOpenEnderChest msg, Supplier<Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Context context = ctx.get();
                ServerPlayerEntity player = context.getSender();

                // guard against player not having an ender chest
                if (player == null || !player.inventory.hasItemStack(new ItemStack(Blocks.ENDER_CHEST)))
                    return;

                PortableEnderChest.openContainer(player);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

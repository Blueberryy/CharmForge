package svenhjol.charm.message;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import svenhjol.charm.base.iface.ICharmMessage;
import svenhjol.charm.module.PortableCrafting;

import java.util.function.Supplier;

@SuppressWarnings("EmptyMethod")
public class ServerOpenCrafting implements ICharmMessage {
    public static void encode(ServerOpenCrafting msg, PacketBuffer buf) { }

    public static ServerOpenCrafting decode(PacketBuffer buf) {
        return new ServerOpenCrafting();
    }

    public static class Handler {
        public static void handle(final ServerOpenCrafting msg, Supplier<Context> ctx) {
            ctx.get().enqueueWork(() -> {
                Context context = ctx.get();
                ServerPlayerEntity player = context.getSender();

                // guard against player not having a crafting table
                if (player == null || !player.inventory.hasItemStack(new ItemStack(Blocks.CRAFTING_TABLE)))
                    return;

                PortableCrafting.openContainer(player);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}

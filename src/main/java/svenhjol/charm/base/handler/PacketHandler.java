package svenhjol.charm.base.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import svenhjol.charm.Charm;
import svenhjol.charm.base.iface.ICharmMessage;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class PacketHandler {
    private int index = 0;
    private final SimpleChannel channel;

    public PacketHandler() {
        this("main", 1);
    }

    public PacketHandler(String channelName, int protocol) {
        String s = String.valueOf(protocol);

        this.channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Charm.MOD_ID, channelName))
            .clientAcceptedVersions(s::equals)
            .serverAcceptedVersions(s::equals)
            .networkProtocolVersion(() -> s)
            .simpleChannel();
    }

    public <MSG> void register(Class<MSG> clazz, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        Charm.LOG.debug("Registering message " + clazz + ", index " + index);
        channel.registerMessage(index, clazz, encoder, decoder, messageConsumer);
        index++;
    }

    public void sendToAll(ICharmMessage msg) {
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendToPlayer(msg, player);
        }
    }

    public void sendNonLocal(ICharmMessage msg, ServerPlayerEntity player) {
        if (player.server.isDedicatedServer()
            || !player.getGameProfile().getName().equals(player.server.getServerOwner())
        ) {
            this.channel.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    /**
     * Send from client to server. Must be called client-side.
     *
     * @param msg Message to send
     */
    public void sendToServer(ICharmMessage msg) {
        this.channel.sendToServer(msg);
    }

    /**
     * Send to specific player. Must be called server-side.
     *
     * @param msg    Message to send
     * @param player Player to send to
     */
    public void sendToPlayer(ICharmMessage msg, ServerPlayerEntity player) {
        if (!(player instanceof FakePlayer))
            this.channel.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}

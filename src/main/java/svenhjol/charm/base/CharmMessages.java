package svenhjol.charm.base;

import svenhjol.charm.Charm;
import svenhjol.charm.message.*;

public class CharmMessages {
    public static void init() {
        Charm.PACKET_HANDLER.register(ServerOpenCrafting.class, ServerOpenCrafting::encode, ServerOpenCrafting::decode, ServerOpenCrafting.Handler::handle);
        Charm.PACKET_HANDLER.register(ServerOpenEnderChest.class, ServerOpenEnderChest::encode, ServerOpenEnderChest::decode, ServerOpenEnderChest.Handler::handle);
        Charm.PACKET_HANDLER.register(ServerSortInventory.class, ServerSortInventory::encode, ServerSortInventory::decode, ServerSortInventory.Handler::handle);
        Charm.PACKET_HANDLER.register(ServerUpdatePlayerState.class, ServerUpdatePlayerState::encode, ServerUpdatePlayerState::decode, ServerUpdatePlayerState.Handler::handle);
        Charm.PACKET_HANDLER.register(ClientSetGlowingEntities.class, ClientSetGlowingEntities::encode, ClientSetGlowingEntities::decode, ClientSetGlowingEntities.Handler::handle);
        Charm.PACKET_HANDLER.register(ClientOpenInventory.class, ClientOpenInventory::encode, ClientOpenInventory::decode, ClientOpenInventory.Handler::handle);
        Charm.PACKET_HANDLER.register(ClientUpdatePlayerState.class, ClientUpdatePlayerState::encode, ClientUpdatePlayerState::decode, ClientUpdatePlayerState.Handler::handle);
        Charm.PACKET_HANDLER.register(ServerAtlasTransfer.class, ServerAtlasTransfer::encode, ServerAtlasTransfer::decode, ServerAtlasTransfer.Handler::handle);
    }
}

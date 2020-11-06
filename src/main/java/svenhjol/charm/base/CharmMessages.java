package svenhjol.charm.base;

import svenhjol.charm.Charm;
import svenhjol.charm.message.ClientOpenInventory;
import svenhjol.charm.message.ClientSetGlowingEntities;

public class CharmMessages {
    public static void init() {
        Charm.PACKET_HANDLER.register(ClientSetGlowingEntities.class, ClientSetGlowingEntities::encode, ClientSetGlowingEntities::decode, ClientSetGlowingEntities.Handler::handle);
        Charm.PACKET_HANDLER.register(ClientOpenInventory.class, ClientOpenInventory::encode, ClientOpenInventory::decode, ClientOpenInventory.Handler::handle);
    }
}

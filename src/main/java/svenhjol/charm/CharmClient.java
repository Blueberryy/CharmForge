package svenhjol.charm;

import svenhjol.charm.base.handler.ClientHandler;
import svenhjol.charm.base.handler.LogHandler;

public class CharmClient {
    public static LogHandler LOG = new LogHandler("CharmClient");

    public CharmClient() {
        ClientHandler.init();
    }
}

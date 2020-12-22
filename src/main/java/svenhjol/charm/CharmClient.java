package svenhjol.charm;

import svenhjol.charm.base.CharmClientLoader;
import svenhjol.charm.base.handler.LogHandler;

public class CharmClient {
    public static LogHandler LOG = new LogHandler("CharmClient");

    public CharmClient() {
        new CharmClientLoader(Charm.MOD_ID);
    }
}

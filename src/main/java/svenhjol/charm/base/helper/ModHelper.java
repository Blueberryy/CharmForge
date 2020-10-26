package svenhjol.charm.base.helper;

import net.minecraftforge.fml.ModList;

public class ModHelper {
    public static boolean isLoaded(String mod) {
        ModList modList = ModList.get();
        return modList.isLoaded(mod);
    }
}

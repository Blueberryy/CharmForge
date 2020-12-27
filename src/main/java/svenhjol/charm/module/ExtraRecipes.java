package svenhjol.charm.module;

import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Adds custom recipes.")
public class ExtraRecipes extends CharmModule {
    @Config(name = "Gilded Blackstone", description = "If true, adds a recipe for Gilded Blackstone using gold nuggets and blackstone.")
    public static boolean useGildedBlackstone = true;

    @Config(name = "Trident", description = "If true, adds a recipe for the Trident using prismarine shards and crystals.")
    public static boolean useTrident = true;

    @Config(name = "Cyan Dye", description = "If true, adds a recipe for Cyan Dye using warped roots.")
    public static boolean useCyanDye = true;

    @Config(name = "Green Dye", description = "If true, adds a recipe for Green Dye using yellow and blue dyes.")
    public static boolean useGreenDye = true;

    @Config(name = "Soul Torch", description = "If true, adds a recipe for Soul Torches using soul sand/soul soil and sticks.")
    public static boolean useSoulTorch = true;

}


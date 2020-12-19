package svenhjol.charm.module;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IntReferenceHolder;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

import java.util.Random;

@Module(mod = Charm.MOD_ID, description = "Removes minimum and maximum XP costs on the anvil. Anvils are also less likely to break.")
public class AnvilImprovements extends CharmModule {
    @Config(name = "Remove Too Expensive", description = "If true, removes the maximum cost of 40 XP when working items on the anvil.")
    public static boolean removeTooExpensive = true;

    @Config(name = "Stronger anvils", description = "If true, anvils are 50% less likely to take damage when used.")
    public static boolean strongerAnvils = true;

    public static boolean allowTooExpensive() {
        return ModuleHandler.enabled(AnvilImprovements.class) && AnvilImprovements.removeTooExpensive;
    }

    public static boolean allowTakeWithoutXp(PlayerEntity player, IntReferenceHolder levelCost) {
        return ModuleHandler.enabled(AnvilImprovements.class)
            && (player.abilities.isCreativeMode || ((player.experienceLevel >= levelCost.get()) && levelCost.get() > -1));
    }

    public static boolean tryDamageAnvil() {
        return ModuleHandler.enabled(AnvilImprovements.class)
            && AnvilImprovements.strongerAnvils
            && new Random().nextFloat() < 0.5F;
    }
}

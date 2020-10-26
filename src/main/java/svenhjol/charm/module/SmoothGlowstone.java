package svenhjol.charm.module;

import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.block.SmoothGlowstoneBlock;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Module;

import java.util.ArrayList;
import java.util.List;

@Module(mod = Charm.MOD_ID, description = "Smelt glowstone to get smooth glowstone.")
public class SmoothGlowstone extends CharmModule {
    public static SmoothGlowstoneBlock SMOOTH_GLOWSTONE;

    @Override
    public void register() {
        SMOOTH_GLOWSTONE = new SmoothGlowstoneBlock(this);
    }

    @Override
    public List<ResourceLocation> getRecipesToRemove() {
        List<ResourceLocation> remove = new ArrayList<>();

        if (!ModuleHandler.enabled("charm:kilns"))
            remove.add(new ResourceLocation(Charm.MOD_ID, "smooth_glowstone/smooth_glowstone_from_firing"));

        return remove;
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PortableCraftingClient;
import svenhjol.charm.container.PortableCraftingContainer;

@Module(mod = Charm.MOD_ID, client = PortableCraftingClient.class, description = "Allows crafting from inventory if the player has a crafting table in their inventory.", hasSubscriptions = true)
public class PortableCrafting extends CharmModule {
    private static final ITextComponent LABEL = new TranslationTextComponent("container.charm.portable_crafting_table");

    @Config(name = "Enable keybind", description = "If true, sets a keybind for opening the portable crafting table (defaults to 'c').")
    public static boolean enableKeybind = true;

    public static void openContainer(ServerPlayerEntity player) {
        player.openContainer(new SimpleNamedContainerProvider((i, inv, p) -> new PortableCraftingContainer(i, inv, IWorldPosCallable.of(p.world, p.getPosition())), LABEL));
    }
}

package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PortableCraftingClient;
import svenhjol.charm.container.PortableCraftingScreenHandler;

@Module(mod = Charm.MOD_ID, description = "Allows crafting from inventory if the player has a crafting table in their inventory.")
public class PortableCrafting extends CharmModule {
    private static final ITextComponent LABEL = new TranslationTextComponent("container.charm.portable_crafting_table");
    public static PortableCraftingClient client;

    @Config(name = "Enable keybind", description = "If true, sets a keybind for opening the portable crafting table (defaults to 'c').")
    public static boolean enableKeybind = true;

    @Override
    public void clientInit() {
        client = new PortableCraftingClient(this);
        ModuleHandler.FORGE_EVENT_BUS.register(client);
    }

    public static void openContainer(ServerPlayerEntity player) {
        player.openContainer(new SimpleNamedContainerProvider((i, inv, p) -> new PortableCraftingScreenHandler(i, inv, IWorldPosCallable.of(p.world, p.getPosition())), LABEL));
    }
}

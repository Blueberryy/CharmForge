package svenhjol.charm.module;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.IWorldPosCallable;
import net.minecraft.screen.SimpleNamedContainerProvider;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslationTextComponent;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.client.PortableCraftingClient;
import svenhjol.charm.container.PortableCraftingScreenHandler;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;

@Module(mod = Charm.MOD_ID, description = "Allows crafting from inventory if the player has a crafting table in their inventory.")
public class PortableCrafting extends CharmModule {
    private static final Text LABEL = new TranslationTextComponent("container.charm.portable_crafting_table");
    public static final ResourceLocation MSG_SERVER_OPEN_CRAFTING = new ResourceLocation(Charm.MOD_ID, "server_open_crafting");
    public static PortableCraftingClient client;

    @Config(name = "Enable keybind", description = "If true, sets a keybind for opening the portable crafting table (defaults to 'c').")
    public static boolean enableKeybind = true;

    @Override
    public void init() {
        // listen for network requests to open the portable ender chest
        ServerSidePacketRegistry.INSTANCE.register(MSG_SERVER_OPEN_CRAFTING, (context, data) -> {
            context.getTaskQueue().execute(() -> {
                ServerPlayerEntity player = (ServerPlayerEntity)context.getPlayer();
                if (player == null || !player.inventory.contains(new ItemStack(Blocks.CRAFTING_TABLE)))
                    return;

                PortableCrafting.openContainer(player);
            });
        });
    }

    @Override
    public void clientInit() {
        client = new PortableCraftingClient(this);
    }

    public static void openContainer(ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedContainerProvider((i, inv, p) -> new PortableCraftingScreenHandler(i, inv, IWorldPosCallable.create(p.world, p.getBlockPos())), LABEL));
    }
}

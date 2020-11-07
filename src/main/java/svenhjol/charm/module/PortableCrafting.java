package svenhjol.charm.module;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PortableCraftingClient;
import svenhjol.charm.container.PortableCraftingScreenHandler;

@Module(mod = Charm.MOD_ID, description = "Allows crafting from inventory if the player has a crafting table in their inventory.")
public class PortableCrafting extends CharmModule {
    private static final ITextComponent LABEL = new TranslationTextComponent("container.charm.portable_crafting_table");
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
                if (player == null || !player.inventory.hasItemStack(new ItemStack(Blocks.CRAFTING_TABLE)))
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
        player.openContainer(new SimpleNamedContainerProvider((i, inv, p) -> new PortableCraftingScreenHandler(i, inv, IWorldPosCallable.of(p.world, p.getPosition())), LABEL));
    }
}

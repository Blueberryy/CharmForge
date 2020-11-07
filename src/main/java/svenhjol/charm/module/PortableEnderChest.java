package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PortableEnderChestClient;
import svenhjol.charm.container.PortableEnderChestScreenHandler;

@Module(mod = Charm.MOD_ID, description = "Allows access to chest contents if the player has an Ender Chest in their inventory.")
public class PortableEnderChest extends CharmModule {
    private static final ITextComponent LABEL = new TranslationTextComponent("container.charm.portable_ender_chest");
    public static final ResourceLocation MSG_SERVER_OPEN_ENDER_CHEST = new ResourceLocation(Charm.MOD_ID, "server_open_ender_chest");
    public static PortableEnderChestClient client;

    @Config(name = "Enable keybind", description = "If true, sets a keybind for opening the portable Ender Chest (defaults to 'b').")
    public static boolean enableKeybind = true;

    @Override
    public void clientInit() {
        client = new PortableEnderChestClient(this);
        ModuleHandler.FORGE_EVENT_BUS.register(client);
    }

    public static void openContainer(ServerPlayerEntity player) {
        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 0.4F, 1.08F);
        player.openContainer(new SimpleNamedContainerProvider((i, inv, p) -> new PortableEnderChestScreenHandler(i, inv, p.getInventoryEnderChest()), LABEL));
    }
}

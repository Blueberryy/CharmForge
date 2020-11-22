package svenhjol.charm.module;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.client.PortableEnderChestClient;
import svenhjol.charm.container.PortableEnderChestContainer;

@Module(mod = Charm.MOD_ID, client = PortableEnderChestClient.class, description = "Allows access to chest contents if the player has an Ender Chest in their inventory.", hasSubscriptions = true)
public class PortableEnderChest extends CharmModule {
    private static final ITextComponent LABEL = new TranslationTextComponent("container.charm.portable_ender_chest");

    @Config(name = "Enable keybind", description = "If true, sets a keybind for opening the portable Ender Chest (defaults to 'b').")
    public static boolean enableKeybind = true;

    public static void openContainer(ServerPlayerEntity player) {
        player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ENDER_CHEST_OPEN, SoundCategory.PLAYERS, 0.4F, 1.08F);
        player.openContainer(new SimpleNamedContainerProvider((i, inv, p) -> new PortableEnderChestContainer(i, inv, p.getInventoryEnderChest()), LABEL));
    }
}

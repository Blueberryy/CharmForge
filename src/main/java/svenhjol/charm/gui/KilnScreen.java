package svenhjol.charm.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.container.KilnScreenHandler;

@OnlyIn(Dist.CLIENT)
public class KilnScreen extends AbstractFurnaceScreen<KilnScreenHandler> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

    public KilnScreen(KilnScreenHandler container, PlayerInventory inventory, Text title) {
        super(container, new KilnRecipeBookScreen(), inventory, title, TEXTURE);
    }
}

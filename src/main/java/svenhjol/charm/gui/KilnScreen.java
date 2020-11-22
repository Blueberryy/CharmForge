package svenhjol.charm.gui;

import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.container.KilnContainer;

@OnlyIn(Dist.CLIENT)
public class KilnScreen extends AbstractFurnaceScreen<KilnContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/smoker.png");

    public KilnScreen(KilnContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, new KilnRecipeBookScreen(), inventory, title, TEXTURE);
    }
}

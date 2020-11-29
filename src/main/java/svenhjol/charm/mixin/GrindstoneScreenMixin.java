package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import svenhjol.charm.client.ExtractEnchantmentsClient;

@Mixin(GrindstoneScreen.class)
public abstract class GrindstoneScreenMixin<T extends GrindstoneContainer> extends ContainerScreen<T> {
    public GrindstoneScreenMixin(T handler, PlayerInventory inventory, ITextComponent title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
        GrindstoneScreen screen = (GrindstoneScreen)(Object)this;
        ExtractEnchantmentsClient.updateGrindstoneCost(screen, this.playerInventory.player, matrices, this.font, this.xSize);
        super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);
    }
}

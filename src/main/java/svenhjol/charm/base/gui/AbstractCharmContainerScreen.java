package svenhjol.charm.base.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import svenhjol.charm.base.CharmResources;

public abstract class AbstractCharmContainerScreen<T extends Container> extends ContainerScreen<T> {
    protected final ResourceLocation texture;

    public AbstractCharmContainerScreen(T screenContainer, PlayerInventory inv, ITextComponent titleIn, ResourceLocation texture) {
        super(screenContainer, inv, titleIn);
        this.passEvents = true;
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        renderHoveredTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
        this.font.func_238422_b_(matrices, this.title.func_241878_f(), 8.0F, 6.0F, 4210752);
        this.font.func_238422_b_(matrices, this.playerInventory.getDisplayName().func_241878_f(), 8.0F, (float) ySize - 94, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        if (minecraft != null) {
            minecraft.getTextureManager().bindTexture(texture);

            int x = (width - xSize) / 2;
            int y = (height - ySize) / 2;
            blit(matrices, x, y, 0, 0, xSize, ySize);
        }
    }
}

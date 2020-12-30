package svenhjol.charm.base.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class CharmImageButton extends ImageButton {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffHighlight;
    private final int yDiffDisabled;

    public CharmImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int yDiffHighlight, int yDiffDisabled, ResourceLocation resourceLocation, IPressable onPress) {
        super(x, y, width, height, xTexStart, yTexStart, yDiffHighlight, resourceLocation, onPress);
        this.resourceLocation = resourceLocation;
        this.xTexStart = xTexStart;
        this.yTexStart = yTexStart;
        this.yDiffHighlight = yDiffHighlight;
        this.yDiffDisabled = yDiffDisabled;

    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float mouseDelta) {
        Minecraft lvt_5_1_ = Minecraft.getInstance();
        lvt_5_1_.getTextureManager().bindTexture(resourceLocation);
        int yTex = yTexStart;
        if (!active) {
            yTex += yDiffDisabled;
        } else if (isHovered()) {
            yTex += yDiffHighlight;
        }

        RenderSystem.enableDepthTest();
        blit(matrices, x, y, xTexStart, yTex, width, height, 256, 256);
        if (isHovered()) {
            renderToolTip(matrices, mouseX, mouseY);
        }

    }
}

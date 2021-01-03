package svenhjol.charm.base.helper;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.function.Consumer;

public class MapRenderHelper {
    private static final RenderType MAP_BACKGROUND = RenderType.getText(new ResourceLocation("textures/map/map_background.png"));

    public static void renderMapWithBackground(MatrixStack matrices, int x, int y, int z, float scale, int light, Consumer<IRenderTypeBuffer.Impl> renderMap) {
        matrices.push();
        matrices.translate(x, y, z);
        matrices.scale(scale, scale, 1);
        IRenderTypeBuffer.Impl bufferSource = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        drawBackgroundVertex(matrices, light, bufferSource.getBuffer(MAP_BACKGROUND));
        renderMap.accept(bufferSource);
        bufferSource.finish();
        matrices.pop();
    }

    public static void drawBackgroundVertex(MatrixStack matrices, int light, IVertexBuilder background) {
        Matrix4f matrix4f = matrices.getLast().getMatrix();
        background.pos(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(light).endVertex();
        background.pos(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(light).endVertex();
        background.pos(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(light).endVertex();
        background.pos(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(light).endVertex();
    }


}

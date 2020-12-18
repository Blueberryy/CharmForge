package svenhjol.charm.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;

import java.util.List;

public class MapTooltipClient extends CharmClientModule {
    private static final RenderType MAP_BACKGROUND = RenderType.getText(new ResourceLocation("textures/map/map_background.png"));

    public MapTooltipClient(CharmModule module) {
        super(module);
    }


    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent.PostBackground event) {
        handleRenderTooltip(event.getMatrixStack(), event.getStack(), event.getLines(), event.getX(), event.getY());
    }

    private boolean handleRenderTooltip(MatrixStack matrices, ItemStack stack, List<? extends ITextProperties> lines, int tx, int ty) {
        if (stack.getItem() != Items.FILLED_MAP) return false;

        final Minecraft mc = Minecraft.getInstance();
        final World world = mc.world;
        if (world == null) return false;

        MapData data = FilledMapItem.getMapData(stack, world);

        if (data == null) return false;

        int x = tx;
        int y = ty - 72;
        int w = 64;
        int right = x + w;

        if (right > mc.getMainWindow().getScaledWidth())
            x = mc.getMainWindow().getScaledWidth() - w;

        if (y < 0)
            y = ty + lines.size() * 10 + 8;

        int light = 240;

        matrices.push();
        matrices.translate(x, y, 0.0);
        matrices.scale(0.5F, 0.5F, 1.0F);
        IRenderTypeBuffer.Impl bufferSource = mc.getRenderTypeBuffers().getBufferSource();
        final IVertexBuilder builder = bufferSource.getBuffer(MAP_BACKGROUND);
        Matrix4f matrix4f = matrices.getLast().getMatrix();
        builder.pos(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 1.0F).lightmap(light).endVertex();
        builder.pos(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 1.0F).lightmap(light).endVertex();
        builder.pos(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(1.0F, 0.0F).lightmap(light).endVertex();
        builder.pos(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).tex(0.0F, 0.0F).lightmap(light).endVertex();
        matrices.push();
        matrices.translate(0.0, 0.0, 1.0);
        mc.gameRenderer.getMapItemRenderer().renderMap(matrices, bufferSource, data, false, light);
        matrices.pop();
        matrices.pop();
        return true;
    }
}


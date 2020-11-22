package svenhjol.charm.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.util.math.vector.Vector3f;
import svenhjol.charm.entity.MoobloomEntity;

public class MoobloomFlowerFeatureRenderer<T extends MoobloomEntity> extends LayerRenderer<T, CowModel<T>> {
    public MoobloomFlowerFeatureRenderer(IEntityRenderer<T, CowModel<T>> context) {
        super(context);
    }

    // copypasta from MooshroomMushroomFeatureRenderer with adjustments to scale and another flower added
    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.isChild() && !entity.isInvisible()) {
            BlockRendererDispatcher blockRenderManager = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState state = entity.getMoobloomType().getFlower();
            int m = LivingRenderer.getPackedOverlay(entity, 0.0F);


            matrixStack.push();
            matrixStack.translate(0.2D, -0.35D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStack.scale(-0.75F, -0.75F, 0.75F);
            matrixStack.translate(-0.5D, -0.65D, -0.5D);
            blockRenderManager.renderBlock(state, matrixStack, vertexConsumerProvider, light, m);
            matrixStack.pop();


            matrixStack.push();
            matrixStack.translate(0.2D, -0.35D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(42.0F));
            matrixStack.translate(0.4D, 0.0D, -0.6D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStack.scale(-0.75F, -0.75F, 0.75F);
            matrixStack.translate(-0.5D, -0.65D, -0.5D);
            blockRenderManager.renderBlock(state, matrixStack, vertexConsumerProvider, light, m);
            matrixStack.pop();


            matrixStack.push();
            matrixStack.translate(0.2D, -0.35D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(42.0F));
            matrixStack.translate(-0.05, 0.0D, -0.4D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(-48.0F));
            matrixStack.scale(-0.75F, -0.75F, 0.75F);
            matrixStack.translate(-0.5D, -0.65D, -0.5D);
            blockRenderManager.renderBlock(state, matrixStack, vertexConsumerProvider, light, m);
            matrixStack.pop();


            if (entity.isPollinated()) {
                matrixStack.push();
                (this.getEntityModel()).getHead().translateRotate(matrixStack);
                matrixStack.translate(0.0D, -0.699999988079071D, -0.20000000298023224D);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-78.0F));
                matrixStack.scale(-0.75F, -0.75F, 0.75F);
                matrixStack.translate(-0.5D, -0.65D, -0.5D);
                blockRenderManager.renderBlock(state, matrixStack, vertexConsumerProvider, light, m);
                matrixStack.pop();
            }
        }
    }
}

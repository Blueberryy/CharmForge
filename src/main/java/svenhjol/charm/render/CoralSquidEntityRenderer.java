package svenhjol.charm.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import svenhjol.charm.entity.CoralSquidEntity;
import svenhjol.charm.model.CoralSquidEntityModel;

public class CoralSquidEntityRenderer extends MobRenderer<CoralSquidEntity, CoralSquidEntityModel<CoralSquidEntity>> {
    public CoralSquidEntityRenderer(EntityRendererManager entityRenderDispatcher) {
        super(entityRenderDispatcher, new CoralSquidEntityModel<>(), 0.7F);
    }

    @Override
    public ResourceLocation getEntityTexture(CoralSquidEntity entity) {
        return entity.getTexture();
    }

    @Override
    protected void preRenderCallback(CoralSquidEntity entity, MatrixStack matrixStack, float f) {
        super.preRenderCallback(entity, matrixStack, f);
        matrixStack.scale(0.8F, 0.8F, 0.8F);
    }

    /**
     * Copypasta from SquidEntityRenderer.
     */
    @Override
    protected void applyRotations(CoralSquidEntity squidEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = MathHelper.lerp(h, squidEntity.prevTiltAngle, squidEntity.tiltAngle);
        float j = MathHelper.lerp(h, squidEntity.prevRollAngle, squidEntity.rollAngle);
        matrixStack.translate(0.0D, 0.25D, 0.0D);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F - g));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(i));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(j));
        matrixStack.translate(0.0D, -1.2000000476837158D, 0.0D);
    }

    @Override
    protected float handleRotationFloat(CoralSquidEntity squidEntity, float f) {
        return MathHelper.lerp(f, squidEntity.prevTentacleAngle, squidEntity.tentacleAngle);
    }
}

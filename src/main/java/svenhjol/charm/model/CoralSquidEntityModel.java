package svenhjol.charm.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

import java.util.Arrays;

public class CoralSquidEntityModel<T extends Entity> extends SegmentedModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer[] tentacles = new ModelRenderer[8];
    private final ImmutableList<ModelRenderer> parts;

    public CoralSquidEntityModel() {
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F);
        ModelRenderer var10000 = this.head;
        var10000.rotationPointY += 4.0F;

        for(int j = 0; j < this.tentacles.length; ++j) {
            this.tentacles[j] = new ModelRenderer(this, 48, 0);
            double d = (double)j * 3.141592653589793D * 2.0D / (double)this.tentacles.length;
            float f = (float)Math.cos(d) * 2.5F;
            float g = (float)Math.sin(d) * 2.5F;
            this.tentacles[j].addBox(-0.5F, 0.0F, -0.5F, 1.0F, 7.0F, 1.0F);
            this.tentacles[j].rotationPointX = f;
            this.tentacles[j].rotationPointZ = g;
            this.tentacles[j].rotationPointY = 7.5F;
            d = (double)j * 3.141592653589793D * -2.0D / (double)this.tentacles.length + 1.5707963267948966D;
            this.tentacles[j].rotateAngleY = (float)d;
        }

        ImmutableList.Builder<ModelRenderer> builder = ImmutableList.builder();
        builder.add(this.head);
        builder.addAll(Arrays.asList(this.tentacles));
        this.parts = builder.build();
    }

    public void setRotationAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        ModelRenderer[] var7 = this.tentacles;
        int var8 = var7.length;

        for(int var9 = 0; var9 < var8; ++var9) {
            ModelRenderer modelPart = var7[var9];
            modelPart.rotateAngleX = animationProgress;
        }

    }

    public Iterable<ModelRenderer> getParts() {
        return this.parts;
    }
}

package svenhjol.charm.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.entity.MoobloomEntity;

@OnlyIn(Dist.CLIENT)
public class MoobloomEntityRenderer extends MobRenderer<MoobloomEntity, CowModel<MoobloomEntity>> {
    public MoobloomEntityRenderer(EntityRendererManager entityRenderDispatcher) {
        super(entityRenderDispatcher, new CowModel<>(), 0.7F);
        this.addLayer(new MoobloomFlowerFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getEntityTexture(MoobloomEntity entity) {
        return entity.getMoobloomTexture();
    }
}

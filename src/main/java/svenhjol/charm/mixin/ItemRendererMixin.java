package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.handler.ColoredGlintHandler;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    private ItemStack itemStackToRender;

    @Inject(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "HEAD"
        )
    )
    private void hookRenderItemHead(ItemStack stack, ItemCameraTransforms.TransformType renderMode, boolean leftHanded, MatrixStack matrices, IRenderTypeBuffer vertexConsumers, int light, int overlay, IBakedModel model, CallbackInfo ci) {
        this.itemStackToRender = stack;
    }

    @Redirect(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;getEntityGlintVertexBuilder(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
        )
    )
    private IVertexBuilder hookRenderItemGetDirectGlintConsumer(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint) {
        return ColoredGlintHandler.getDirectItemGlintConsumer(provider, layer, solid, glint, this.itemStackToRender);
    }

    @Redirect(
        method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;getBuffer(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
        )
    )
    private IVertexBuilder hookRenderItemGetItemGlintConsumer(IRenderTypeBuffer vertexConsumers, RenderType layer, boolean solid, boolean glint) {
        return ColoredGlintHandler.getItemGlintConsumer(vertexConsumers, layer, solid, glint, this.itemStackToRender);
    }
}

package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.handler.ColoredGlintHandler;

@Mixin(ItemStackTileEntityRenderer.class)
public class ItemStackTileEntityRendererMixin {
    private ItemStack itemStackToRender;

    /**
     * Fetch the item stack so we can render the glint further down
     */
    @Inject(
        method = "func_239207_a_",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    private void hookRender(ItemStack stack, ItemCameraTransforms.TransformType mode, MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        this.itemStackToRender = stack;
    }

    /**
     * Redirect shield and trident glint consumer calls to Charm's ColoredGlintHandler
     */
    @Redirect(
        method = "func_239207_a_",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;getEntityGlintVertexBuilder(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
        )
    )
    private IVertexBuilder hookRenderGetDirectGlintConsumer(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint) {
        return ColoredGlintHandler.getDirectItemGlintConsumer(provider, layer, solid, glint, this.itemStackToRender);
    }
}

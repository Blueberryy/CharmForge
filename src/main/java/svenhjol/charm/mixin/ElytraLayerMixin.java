package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin<T extends LivingEntity> {
    private ItemStack itemStackToRender;

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void hookRender(MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        this.itemStackToRender = livingEntity.getItemStackFromSlot(EquipmentSlotType.CHEST);
    }

    /**
     * Deprecated hook, causes crashes with Optifine
     */
//    @Redirect(
//        method = "render",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/client/renderer/ItemRenderer;getArmorVertexBuilder(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
//        )
//    )
//    private IVertexBuilder hookRender(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint) {
//        return ColoredGlintHandler.getArmorGlintRenderLayer(provider, layer, solid, glint, this.itemStackToRender);
//    }
}

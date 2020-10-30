package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.handler.ColoredGlintHandler;

@Mixin(ElytraLayer.class)
public class ElytraFeatureRendererMixin<T extends LivingEntity> {
    private ItemStack itemStackToRender;

    @Inject(
        method = "render",
        at = @At("HEAD")
    )
    private void hookRender(MatrixStack matrixStack, IRenderTypeBuffer vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        this.itemStackToRender = livingEntity.getItemStackFromSlot(EquipmentSlotType.CHEST);
    }

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;getArmorVertexBuilder(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
        )
    )
    private IVertexBuilder hookRender(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint) {
        return ColoredGlintHandler.getArmorGlintConsumer(provider, layer, solid, glint, this.itemStackToRender);
    }
}

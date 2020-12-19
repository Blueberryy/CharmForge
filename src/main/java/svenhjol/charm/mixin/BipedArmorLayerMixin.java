package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import svenhjol.charm.base.handler.ModuleHandler;
import svenhjol.charm.handler.ColoredGlintHandler;
import svenhjol.charm.module.ArmorInvisibility;

@Mixin(BipedArmorLayer.class)
public class BipedArmorLayerMixin<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> {
    @Inject(
        method = "func_241739_a_",
        at = @At("HEAD"),
        cancellable = true
    )
    private void hookRenderArmor(MatrixStack matrices, IRenderTypeBuffer vertexConsumers, T livingEntity, EquipmentSlotType equipmentSlot, int i, A bipedEntityModel, CallbackInfo ci) {
        if (ModuleHandler.enabled(ArmorInvisibility.class)) {
            ItemStack stack = livingEntity.getItemStackFromSlot(equipmentSlot);
            if (ArmorInvisibility.shouldArmorBeInvisible(livingEntity, stack))
                ci.cancel();
        }

        // take a reference to the item being rendered, this is needed for the glint consumer
        ColoredGlintHandler.targetStack = livingEntity.getItemStackFromSlot(equipmentSlot);
    }

    /**
     * Deprecated hook, causes crashes with Optifine
     */
//    @Redirect(
//        method = "func_241738_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IZLnet/minecraft/client/renderer/entity/model/BipedModel;FFFLnet/minecraft/util/ResourceLocation;)V",
//        at = @At(
//            value = "INVOKE",
//            target = "Lnet/minecraft/client/renderer/ItemRenderer;getArmorVertexBuilder(Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
//        )
//    )
//    private IVertexBuilder hookRenderArmorParts(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint) {
//        return ColoredGlintHandler.getArmorGlintConsumer(provider, layer, solid, glint, this.itemStackToRender);
//    }
}

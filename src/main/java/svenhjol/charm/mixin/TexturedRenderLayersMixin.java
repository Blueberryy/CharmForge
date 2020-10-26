package svenhjol.charm.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.render.VariantChestBlockEntityRenderer;

@Mixin(TexturedRenderLayers.class)
public class TexturedRenderLayersMixin {
    @Inject(
        method = "getChestTexture(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/block/enums/ChestType;Z)Lnet/minecraft/client/util/SpriteResourceLocation;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetChestTexture(BlockEntity blockEntity, ChestType type, boolean christmas, CallbackInfoReturnable<SpriteResourceLocation> cir) {
        SpriteResourceLocation spriteResourceLocation = VariantChestBlockEntityRenderer.getMaterial(blockEntity, type);
        if (spriteResourceLocation != null)
            cir.setReturnValue(spriteResourceLocation);
    }
}
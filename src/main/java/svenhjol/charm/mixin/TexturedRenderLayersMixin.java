package svenhjol.charm.mixin;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.render.VariantChestTileEntityRenderer;

@Mixin(Atlases.class)
public class TexturedRenderLayersMixin {
    @Inject(
        method = "getChestMaterial(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/state/properties/ChestType;Z)Lnet/minecraft/client/renderer/model/RenderMaterial;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void hookGetChestTexture(TileEntity tile, ChestType type, boolean christmas, CallbackInfoReturnable<RenderMaterial> cir) {
        RenderMaterial spriteResourceLocation = VariantChestTileEntityRenderer.getChestMaterial(tile, type);
        if (spriteResourceLocation != null)
            cir.setReturnValue(spriteResourceLocation);
    }
}
package svenhjol.charm.mixin;

import net.minecraft.util.Rotation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import svenhjol.charm.base.helper.StructureHelper;

@Mixin(SingleJigsawPiece.class)
public class SingleJigsawPieceMixin {
    /**
     * Adds all the structure processors defined in StructureHelper.SINGLE_POOL_ELEMENT_PROCESSORS
     * to the SinglePoolElement when the placement is being initialized.
     */
    @Inject(
        method = "func_230379_a_",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/gen/feature/template/PlacementSettings;addProcessor(Lnet/minecraft/world/gen/feature/template/StructureProcessor;)Lnet/minecraft/world/gen/feature/template/PlacementSettings;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void hookCreatePlacementData(Rotation blockRotation, MutableBoundingBox blockBox, boolean keepJigsaws, CallbackInfoReturnable<PlacementSettings> cir, PlacementSettings placement) {
        StructureHelper.SINGLE_POOL_ELEMENT_PROCESSORS.forEach(placement::addProcessor);
    }
}

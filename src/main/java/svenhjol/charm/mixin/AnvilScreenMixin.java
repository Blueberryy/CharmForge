package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.module.AnvilImprovements;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {
    @Redirect(
        method = "drawGuiContainerForegroundLayer",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/entity/player/PlayerAbilities;isCreativeMode:Z"
        )
    )
    private boolean hookMaximumCostCheck(PlayerAbilities abilities, MatrixStack matrix, int x, int y) {
        return AnvilImprovements.allowTooExpensive() || abilities.isCreativeMode;
    }
}

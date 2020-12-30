package svenhjol.charm.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.AbstractRepairScreen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.module.AnvilImprovements;

import java.util.List;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends AbstractRepairScreen<RepairContainer> {
    public AnvilScreenMixin(RepairContainer container, PlayerInventory playerInventory, ITextComponent title, ResourceLocation guiTexture) {
        super(container, playerInventory, title, guiTexture);
    }

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

    @Override
    public List<ITextComponent> getTooltipFromItem(ItemStack stack) {
        List<ITextComponent> tooltip = super.getTooltipFromItem(stack);

        if (!AnvilImprovements.showRepairCost)
            return tooltip;

        return AnvilImprovements.addRepairCostToTooltip(stack, tooltip);
    }
}

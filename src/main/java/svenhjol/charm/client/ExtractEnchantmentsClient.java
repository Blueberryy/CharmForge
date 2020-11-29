package svenhjol.charm.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.item.ItemStack;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.module.ExtractEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtractEnchantmentsClient extends CharmClientModule {
    public ExtractEnchantmentsClient(CharmModule module) {
        super(module);
    }

    public static void updateGrindstoneCost(GrindstoneScreen screen, PlayerEntity player, MatrixStack matrices, FontRenderer textRenderer, int width) {
        GrindstoneContainer screenHandler = screen.getContainer();

        // add all slot stacks to list for checking
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(screenHandler.getSlot(0).getStack());
        stacks.add(screenHandler.getSlot(1).getStack());

        // if it's a disenchant operation
        if (ExtractEnchantments.shouldExtract(stacks)) {

            // get the stack to disenchant
            Optional<ItemStack> enchanted = ExtractEnchantments.getEnchantedItemFromStacks(stacks);
            if (!enchanted.isPresent())
                return;

            // get the stack cost and render it
            int cost = ExtractEnchantments.getCost(enchanted.get());

            if (!ExtractEnchantments.hasEnoughXp(player, cost)) {
                String string = I18n.format("container.repair.cost", cost);
                int color = 16736352;
                int k = width - 8 - textRenderer.getStringWidth(string) - 2;
                AbstractGui.fill(matrices, k - 2, 67, width - 8, 79, 1325400064);
                textRenderer.drawStringWithShadow(matrices, string, (float)k, 69.0F, color);
            }
        }
    }
}

package svenhjol.charm.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmResources;
import svenhjol.charm.base.helper.ItemHelper;
import svenhjol.charm.base.helper.ItemNBTHelper;
import svenhjol.charm.block.CrateBlock;
import svenhjol.charm.gui.CrateScreen;
import svenhjol.charm.handler.TooltipInventoryHandler;
import svenhjol.charm.module.Crates;
import svenhjol.charm.tileentity.CrateTileEntity;

import java.util.ArrayList;
import java.util.List;

public class CratesClient extends CharmClientModule {
    public CratesClient(Crates module) {
        super(module);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Crates.CONTAINER, CrateScreen::new);
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        handleItemTooltip(event.getItemStack(), event.getToolTip());
    }

    @SubscribeEvent
    public void onRenderTooltip(RenderTooltipEvent event) {
        handleRenderTooltip(event.getMatrixStack(), event.getStack(), event.getLines(), event.getX(), event.getY());
    }

    private void handleItemTooltip(ItemStack stack, List<ITextComponent> lines) {
        if (!Crates.showTooltip)
            return;

        if (stack.isEmpty() || ItemHelper.getBlockClass(stack) != CrateBlock.class || stack.getTag() == null || stack.getTag().isEmpty())
            return;

        CompoundNBT tag = ItemNBTHelper.getCompound(stack, "BlockEntityTag", true);

        if (tag != null) {
            if (!tag.contains("id", Constants.NBT.TAG_STRING)) {
                tag = tag.copy();
                tag.putString("id", "charm:crate");
            }
            TileEntity tile = TileEntity.readTileEntity(null, tag);

            if (tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
                List<ITextComponent> toolTipCopy = new ArrayList<>(lines);

                for (int i = 1; i < toolTipCopy.size(); i++) {
                    final ITextComponent t = toolTipCopy.get(i);
                    final String s = t.getString();

                    // shamelessly lifted from Quark
                    if (!s.startsWith("\u00a7") || s.startsWith("\u00a7o"))
                        lines.remove(t);
                }
            }
        }
    }

    private boolean handleRenderTooltip(MatrixStack matrices, ItemStack stack, List<? extends ITextProperties> lines, int tx, int ty) {
        final Minecraft mc = Minecraft.getInstance();

        if (!stack.hasTag())
            return false;

        CompoundNBT tag = ItemNBTHelper.getCompound(stack, "TileEntityTag", true);

        if (tag == null)
            return false;

        if (!tag.contains("id", 8)) {
            tag = tag.copy();
            tag.putString("id", "charm:crate");
        }
        BlockItem blockItem = (BlockItem) stack.getItem();
        TileEntity tileEntity = TileEntity.readTileEntity(blockItem.getBlock().getDefaultState(), tag);
        if (tileEntity == null)
            return false;

        CrateTileEntity crate = (CrateTileEntity) tileEntity;
        NonNullList<ItemStack> items = crate.getItems();

        int size = crate.getSizeInventory();

        int x = tx - 5;
        int y = ty - 35;
        int w = 172;
        int h = 27;
        int right = x + w;

        if (right > mc.getMainWindow().getScaledWidth())
            x -= (right - mc.getMainWindow().getScaledWidth());

        if (y < 0)
            y = ty + lines.size() * 10 + 5;

        RenderSystem.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableRescaleNormal();
        RenderSystem.color3f(1f, 1f, 1f);
        RenderSystem.translatef(0, 0, 700);
        mc.getTextureManager().bindTexture(CharmResources.SLOT_WIDGET);

        RenderHelper.disableStandardItemLighting();
        TooltipInventoryHandler.renderTooltipBackground(mc, matrices, x, y, 9, 1, -1);
        RenderSystem.color3f(1f, 1f, 1f);

        ItemRenderer render = mc.getItemRenderer();
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableDepthTest();

        for (int i = 0; i < size; i++) {
            ItemStack itemstack;

            try {
                itemstack = items.get(i);
            } catch (Exception e) {
                // catch null issue with itemstack. Needs investigation. #255
                continue;
            }
            int xp = x + 6 + (i % 9) * 18;
            int yp = y + 6 + (i / 9) * 18;

            if (!itemstack.isEmpty()) {
                render.renderItemAndEffectIntoGUI(itemstack, xp, yp);
                render.renderItemOverlays(mc.fontRenderer, itemstack, xp, yp);
            }
        }

        RenderSystem.disableDepthTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        return true;
    }
}


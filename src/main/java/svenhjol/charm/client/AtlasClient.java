package svenhjol.charm.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.container.AtlasInventory;
import svenhjol.charm.gui.AtlasScreen;
import svenhjol.charm.module.Atlas;
import svenhjol.charm.render.AtlasRenderer;

public class AtlasClient extends CharmClientModule {
    private final AtlasRenderer renderer;

    public AtlasClient(CharmModule module) {
        super(module);
        renderer = new AtlasRenderer(Minecraft.getInstance().textureManager);
    }

    @Override
    public void register() {
        ScreenManager.registerFactory(Atlas.CONTAINER, AtlasScreen::new);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() == Atlas.ATLAS_ITEM) {
            renderAtlas(event.getMatrixStack(), event.getBuffers(), event.getLight(), event.getHand(), event.getInterpolatedPitch(), event.getEquipProgress(),
                    event.getSwingProgress(), itemStack);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty() || stack.getItem() != Atlas.ATLAS_ITEM) return;

        PlayerEntity player = event.getPlayer();
        if (player == null) return;

        AtlasInventory inventory = Atlas.getInventory(player.world, stack);
        event.getToolTip().add(new StringTextComponent("Scale " + inventory.getScale()).mergeStyle(TextFormatting.GRAY));
        ItemStack map = inventory.getLastActiveMapItem();
        if (map == null) return;

        IFormattableTextComponent name = map.hasDisplayName() ? map.getDisplayName().deepCopy()
                : map.getDisplayName().deepCopy().append(new StringTextComponent(" #" + FilledMapItem.getMapId(map)));
        event.getToolTip().add(name.mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
    }


    public void renderAtlas(MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, Hand hand, float pitch, float equip, float swing, ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.world;
        ClientPlayerEntity player = minecraft.player;
        if (player == null) return;
        AtlasInventory inventory = Atlas.getInventory(world, stack);

        matrixStack.push(); // needed so that parent renderer isn't affect by what we do here

        // copypasta from renderMapFirstPersonSide
        float e = hand == Hand.MAIN_HAND ? 1.0F : -1.0F;
        matrixStack.translate(e * 0.125F, -0.125D, 0.0D);

        // render player arm
        if (!player.isInvisible()) {
            matrixStack.push();
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(e * 10.0F));
            renderer.renderArm(player, matrixStack, buffers, light, swing, equip, hand);
            matrixStack.pop();
        }

        // transform page based on the hand it is held and render it
        matrixStack.push();
        renderer.transformPageForHand(matrixStack, buffers, light, swing, equip, hand);
        renderer.renderAtlas(world, inventory, matrixStack, buffers, light);
        matrixStack.pop();

        matrixStack.pop(); // close
    }

    public static void updateInventory(int atlasSlot) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player == null) return;
        ItemStack atlas = player.inventory.getStackInSlot(atlasSlot);
        Atlas.getInventory(mc.world, atlas).reload(atlas);
    }
}


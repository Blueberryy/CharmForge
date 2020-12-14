package svenhjol.charm.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.gui.AtlasScreen;
import svenhjol.charm.base.helper.AtlasInventory;
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


    public void renderAtlas(MatrixStack matrixStack, IRenderTypeBuffer buffers, int light, Hand hand, float pitch, float equip, float swing, ItemStack stack) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientWorld world = minecraft.world;
        AtlasInventory inventory = Atlas.getInventory(world, stack);
        ClientPlayerEntity player = minecraft.player;
        if (player == null) return;

        matrixStack.push(); // needed so that parent renderer isn't affect by what we do here

        // copypasta from renderMapFirstPersonSide
        float e = hand == Hand.MAIN_HAND ? 1.0F : -1.0F;
        matrixStack.translate((double) (e * 0.125F), -0.125D, 0.0D);

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
        renderer.renderAtlas(player, inventory, matrixStack, buffers, light);
        matrixStack.pop();

        matrixStack.pop(); // close
    }

}


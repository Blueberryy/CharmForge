package svenhjol.charm.client;

import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmClientModule;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.render.VariantChestTileEntityRenderer;

public class VariantChestsClient extends CharmClientModule {
    public VariantChestsClient(CharmModule module) {
        super(module);
    }

    @Override
    public void register() {
        ClientRegistry.bindTileEntityRenderer(VariantChests.NORMAL_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(VariantChests.TRAPPED_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);
    }

    @Override
    public void textureStitch(TextureStitchEvent event) {
        if (event instanceof TextureStitchEvent.Pre && event.getMap().getTextureLocation().toString().equals("minecraft:textures/atlas/chest.png")) {
            TextureStitchEvent.Pre ev = (TextureStitchEvent.Pre)event;
            VariantChests.NORMAL_CHEST_BLOCKS.keySet().forEach(type -> {
                addChestTexture(ev, type, ChestType.LEFT);
                addChestTexture(ev, type, ChestType.RIGHT);
                addChestTexture(ev, type, ChestType.SINGLE);
            });
        }
    }

    private void addChestTexture(TextureStitchEvent.Pre event, IVariantMaterial variant, ChestType chestType) {
        String chestTypeName = chestType != ChestType.SINGLE ? "_" + chestType.getString().toLowerCase() : "";
        String[] bases = {"trapped", "normal"};

        for (String base : bases) {
            ResourceLocation res = new ResourceLocation(Charm.MOD_ID, "entity/chest/" + variant.getString() + "_" + base + chestTypeName);
            VariantChestTileEntityRenderer.addTexture(variant, chestType, res, base.equals("trapped"));
            event.addSprite(res);
        }
    }
}

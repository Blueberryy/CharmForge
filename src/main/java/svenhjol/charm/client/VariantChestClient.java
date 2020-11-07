package svenhjol.charm.client;

import net.minecraft.block.Block;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.block.VariantChestBlock;
import svenhjol.charm.block.VariantTrappedChestBlock;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.render.VariantChestTileEntityRenderer;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

@OnlyIn(Dist.CLIENT)
public class VariantChestClient {
    private final CharmModule module;
    private final VariantChestTileEntity CACHED_NORMAL_CHEST = new VariantChestTileEntity();
    private final VariantTrappedChestTileEntity CACHED_TRAPPED_CHEST = new VariantTrappedChestTileEntity();

    public VariantChestClient(CharmModule module) {
        this.module = module;
    }

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent event) {
        if (event instanceof TextureStitchEvent.Pre && event.getMap().getTextureLocation().toString().equals("minecraft:textures/atlas/chest.png")) {
            TextureStitchEvent.Pre ev = (TextureStitchEvent.Pre)event;
            VariantChests.NORMAL_CHEST_BLOCKS.keySet().forEach(type -> {
                addChestTexture(ev, type, ChestType.LEFT);
                addChestTexture(ev, type, ChestType.RIGHT);
                addChestTexture(ev, type, ChestType.SINGLE);
            });
        }
    }

    private TileEntity handleBlockItemRender(Block block) {
        if (block instanceof VariantChestBlock) {
            VariantChestBlock chest = (VariantChestBlock)block;
            CACHED_NORMAL_CHEST.setMaterialType(chest.getMaterialType());
            return CACHED_NORMAL_CHEST;

        } else if (block instanceof VariantTrappedChestBlock) {
            VariantTrappedChestBlock chest = (VariantTrappedChestBlock)block;
            CACHED_TRAPPED_CHEST.setMaterialType(chest.getMaterialType());
            return CACHED_TRAPPED_CHEST;
        }

        return null;
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

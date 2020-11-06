package svenhjol.charm.client;

import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.block.VariantChestBlock;
import svenhjol.charm.block.VariantTrappedChestBlock;
import svenhjol.charm.event.BlockItemRenderCallback;
import svenhjol.charm.event.TextureStitchCallback;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.render.VariantChestTileEntityRenderer;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class VariantChestClient {
    private final CharmModule module;
    private final VariantChestTileEntity CACHED_NORMAL_CHEST = new VariantChestTileEntity();
    private final VariantTrappedChestTileEntity CACHED_TRAPPED_CHEST = new VariantTrappedChestTileEntity();

    public VariantChestClient(CharmModule module) {
        this.module = module;

        TileEntityRendererRegistry.INSTANCE.register(VariantChests.NORMAL_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);
        TileEntityRendererRegistry.INSTANCE.register(VariantChests.TRAPPED_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);

        TextureStitchCallback.EVENT.register(((atlas, textures) -> {
            if (atlas.getId().toString().equals("minecraft:textures/atlas/chest.png")) {
                VariantChests.NORMAL_CHEST_BLOCKS.keySet().forEach(type -> {
                    addChestTexture(textures, type, ChestType.LEFT);
                    addChestTexture(textures, type, ChestType.RIGHT);
                    addChestTexture(textures, type, ChestType.SINGLE);
                });
            }
        }));

        BlockItemRenderCallback.EVENT.register(block -> {
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
        });
    }


    private void addChestTexture(Set<ResourceLocation> textures, IVariantMaterial variant, ChestType chestType) {
        String chestTypeName = chestType != ChestType.SINGLE ? "_" + chestType.toString().toLowerCase() : "";
        String[] bases = {"trapped", "normal"};

        for (String base : bases) {
            ResourceLocation id = new ResourceLocation(module.mod, "entity/chest/" + variant.toString() + "_" + base + chestTypeName);
            VariantChestTileEntityRenderer.addTexture(variant, chestType, id, base.equals("trapped"));
            textures.add(id);
        }
    }
}

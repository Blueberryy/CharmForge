package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.helper.ModHelper;
import svenhjol.charm.base.iface.Config;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.VariantChestBlock;
import svenhjol.charm.block.VariantTrappedChestBlock;
import svenhjol.charm.render.VariantChestTileEntityRenderer;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

import java.util.HashMap;
import java.util.Map;

@Module(mod = Charm.MOD_ID, description = "Chests available in all types of vanilla wood.", hasSubscriptions = true)
public class VariantChests extends CharmModule {
    public static final ResourceLocation NORMAL_ID = new ResourceLocation(Charm.MOD_ID, "variant_chest");
    public static final ResourceLocation TRAPPED_ID = new ResourceLocation(Charm.MOD_ID, "trapped_chest");

    public static final Map<IVariantMaterial, VariantChestBlock> NORMAL_CHEST_BLOCKS = new HashMap<>();
    public static final Map<IVariantMaterial, VariantTrappedChestBlock> TRAPPED_CHEST_BLOCKS = new HashMap<>();

    public static TileEntityType<VariantChestTileEntity> NORMAL_BLOCK_ENTITY;
    public static TileEntityType<VariantTrappedChestTileEntity> TRAPPED_BLOCK_ENTITY;

    @Config(name = "Override", description = "This module is automatically disabled if Quark is present. Set true to force enable.")
    public static boolean override = false;

    @Override
    public void register() {
        for (VanillaVariantMaterial type : VanillaVariantMaterial.values()) {
            NORMAL_CHEST_BLOCKS.put(type, new VariantChestBlock(this, type));
            TRAPPED_CHEST_BLOCKS.put(type, new VariantTrappedChestBlock(this, type));
        }

        NORMAL_BLOCK_ENTITY = RegistryHandler.tileEntity(NORMAL_ID, VariantChestTileEntity::new, NORMAL_CHEST_BLOCKS.values().toArray(new Block[0]));
        TRAPPED_BLOCK_ENTITY = RegistryHandler.tileEntity(TRAPPED_ID, VariantTrappedChestTileEntity::new, TRAPPED_CHEST_BLOCKS.values().toArray(new Block[0]));

        depends(!ModHelper.isLoaded("quark") || override);
    }

    @Override
    public void clientRegister() {
        ClientRegistry.bindTileEntityRenderer(NORMAL_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(TRAPPED_BLOCK_ENTITY, VariantChestTileEntityRenderer::new);
    }

    @Override
    public void clientTextureStitch(TextureStitchEvent event) {
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

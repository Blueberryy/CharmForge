package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
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
import svenhjol.charm.client.VariantChestsClient;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

import java.util.HashMap;
import java.util.Map;

@Module(mod = Charm.MOD_ID, client = VariantChestsClient.class, description = "Chests available in all types of vanilla wood.")
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
}

package svenhjol.charm.module;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmModule;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.base.enums.VanillaVariantMaterial;
import svenhjol.charm.base.handler.RegistryHandler;
import svenhjol.charm.base.iface.Module;
import svenhjol.charm.block.VariantChestBlock;
import svenhjol.charm.block.VariantTrappedChestBlock;
import svenhjol.charm.client.VariantChestClient;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

import java.util.HashMap;
import java.util.Map;

@Module(mod = Charm.MOD_ID, description = "Chests available in all types of vanilla wood.")
public class VariantChests extends CharmModule {
    public static final ResourceLocation NORMAL_ID = new ResourceLocation("variant_chest");
    public static final ResourceLocation TRAPPED_ID = new ResourceLocation(Charm.MOD_ID, "trapped_chest");

    public static final Map<IVariantMaterial, VariantChestBlock> NORMAL_CHEST_BLOCKS = new HashMap<>();
    public static final Map<IVariantMaterial, VariantTrappedChestBlock> TRAPPED_CHEST_BLOCKS = new HashMap<>();

    public static TileEntityType<VariantChestTileEntity> NORMAL_BLOCK_ENTITY;
    public static TileEntityType<VariantTrappedChestTileEntity> TRAPPED_BLOCK_ENTITY;

    @Override
    public void register() {
        for (VanillaVariantMaterial type : VanillaVariantMaterial.values()) {
            NORMAL_CHEST_BLOCKS.put(type, new VariantChestBlock(this, type));
            TRAPPED_CHEST_BLOCKS.put(type, new VariantTrappedChestBlock(this, type));
        }

        NORMAL_BLOCK_ENTITY = RegistryHandler.TileEntity(NORMAL_ID, VariantChestTileEntity::new, NORMAL_CHEST_BLOCKS.values().toArray(new Block[0]));
        TRAPPED_BLOCK_ENTITY = RegistryHandler.TileEntity(TRAPPED_ID, VariantTrappedChestTileEntity::new, TRAPPED_CHEST_BLOCKS.values().toArray(new Block[0]));
    }

    @Override
    public void clientRegister() {
        new VariantChestClient(this);
    }
}

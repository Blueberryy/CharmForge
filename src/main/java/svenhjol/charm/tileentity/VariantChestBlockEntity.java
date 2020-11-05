package svenhjol.charm.tileentity;

import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntityType;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.block.IVariantChestBlock;
import svenhjol.charm.module.VariantChests;

import javax.annotation.Nullable;

public class VariantChestTileEntity extends ChestTileEntity {
    private IVariantMaterial materialType = null;

    public VariantChestTileEntity() {
        super(VariantChests.NORMAL_BLOCK_ENTITY);
    }

    protected VariantChestTileEntity(TileEntityType<?> tile) {
        super(tile);
    }

    @Nullable
    public IVariantMaterial getMaterialType() {
        if (materialType == null && world != null)
            return ((IVariantChestBlock)this.getCachedState().getBlock()).getMaterialType();

        return materialType;
    }

    public void setMaterialType(IVariantMaterial materialType) {
        this.materialType = materialType;
    }
}

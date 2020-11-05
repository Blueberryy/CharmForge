package svenhjol.charm.render;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.TileEntity.VariantChestTileEntity;
import svenhjol.charm.TileEntity.VariantTrappedChestTileEntity;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class VariantChestTileEntityRenderer<T extends VariantChestTileEntity> extends ChestTileEntityRenderer<T> {
    private static final Map<IVariantMaterial, Map<ChestType, RenderMaterial>> normalTextures = new HashMap<>();
    private static final Map<IVariantMaterial, Map<ChestType, RenderMaterial>> trappedTextures = new HashMap<>();

    public VariantChestTileEntityRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    public static void addTexture(IVariantMaterial material, ChestType chestType, ResourceLocation id, boolean trapped) {
        Map<IVariantMaterial, Map<ChestType, RenderMaterial>> textures = trapped
            ? VariantChestTileEntityRenderer.trappedTextures
            : VariantChestTileEntityRenderer.normalTextures;

        if (!textures.containsKey(material))
            textures.put(material, new HashMap<>());

        textures.get(material).put(chestType, new RenderMaterial(Atlases.CHEST_ATLAS, id));
    }

    @Nullable
    public static RenderMaterial getChestMaterial(TileEntity tileEntity, ChestType chestType) {
        if (!(tileEntity instanceof VariantChestTileEntity))
            return null;

        Map<IVariantMaterial, Map<ChestType, RenderMaterial>> textures = tileEntity instanceof VariantTrappedChestTileEntity
            ? trappedTextures
            : normalTextures;

        IVariantMaterial material = ((VariantChestTileEntity)tileEntity).getMaterialType();

        if (textures.containsKey(material))
            return textures.get(material).getOrDefault(chestType, null);

        return null;
    }
}

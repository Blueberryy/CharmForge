package svenhjol.charm.render;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.ChestTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.enums.IVariantMaterial;
import svenhjol.charm.tileentity.VariantChestTileEntity;
import svenhjol.charm.tileentity.VariantTrappedChestTileEntity;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("NullableProblems")
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

    @Override
    public RenderMaterial getMaterial(T tileEntity, ChestType chestType) {
        IVariantMaterial material = tileEntity.getMaterialType();

        if (material != null) {
            Map<IVariantMaterial, Map<ChestType, RenderMaterial>> textures = tileEntity instanceof VariantTrappedChestTileEntity
                ? trappedTextures
                : normalTextures;

            if (textures.containsKey(material))
                return textures.get(material).getOrDefault(chestType, Atlases.getChestMaterial(tileEntity, chestType, false));
        }

        return Atlases.getChestMaterial(tileEntity, chestType, false);
    }
}

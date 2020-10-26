package svenhjol.charm.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.ChestBlockEntityRenderer;
import net.minecraft.client.util.SpriteResourceLocation;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.blockentity.VariantChestBlockEntity;
import svenhjol.charm.blockentity.VariantTrappedChestBlockEntity;
import svenhjol.charm.base.enums.IVariantMaterial;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class VariantChestBlockEntityRenderer<T extends VariantChestBlockEntity & ChestAnimationProgress> extends ChestBlockEntityRenderer<T> {
    private static final Map<IVariantMaterial, Map<ChestType, SpriteResourceLocation>> normalTextures = new HashMap<>();
    private static final Map<IVariantMaterial, Map<ChestType, SpriteResourceLocation>> trappedTextures = new HashMap<>();

    public VariantChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    public static void addTexture(IVariantMaterial material, ChestType chestType, ResourceLocation id, boolean trapped) {
        Map<IVariantMaterial, Map<ChestType, SpriteResourceLocation>> textures = trapped
            ? VariantChestBlockEntityRenderer.trappedTextures
            : VariantChestBlockEntityRenderer.normalTextures;

        if (!textures.containsKey(material))
            textures.put(material, new HashMap<>());

        textures.get(material).put(chestType, new SpriteResourceLocation(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, id));
    }

    @Nullable
    public static SpriteResourceLocation getMaterial(BlockEntity blockEntity, ChestType chestType) {
        if (!(blockEntity instanceof VariantChestBlockEntity))
            return null;

        Map<IVariantMaterial, Map<ChestType, SpriteResourceLocation>> textures = blockEntity instanceof VariantTrappedChestBlockEntity
            ? trappedTextures
            : normalTextures;

        IVariantMaterial material = ((VariantChestBlockEntity)blockEntity).getMaterialType();

        if (textures.containsKey(material))
            return textures.get(material).getOrDefault(chestType, null);

        return null;
    }
}

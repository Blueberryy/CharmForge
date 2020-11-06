package svenhjol.charm.handler;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.mixin.accessor.MinecraftAccessor;
import svenhjol.charm.mixin.accessor.RenderStateAccessor;
import svenhjol.charm.mixin.accessor.RenderTypeBuffersMixin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class ColoredGlintHandler {
    public static final String GLINT_TAG = "charm_glint";

    public static Map<DyeColor, ResourceLocation> TEXTURES = new HashMap<>();
    public static Map<DyeColor, RenderType> GLINT = new HashMap<>();
    public static Map<DyeColor, RenderType> ENTITY_GLINT = new HashMap<>();
    public static Map<DyeColor, RenderType> DIRECT_GLINT = new HashMap<>();
    public static Map<DyeColor, RenderType> DIRECT_ENTITY_GLINT = new HashMap<>();
    public static Map<DyeColor, RenderType> ARMOR_GLINT = new HashMap<>();
    public static Map<DyeColor, RenderType> ARMOR_ENTITY_GLINT = new HashMap<>();

    private static boolean hasInit = false;
    private static final boolean changeDefaultGlintColor = false;

    public static void init() {
        if (hasInit)
            return;

        for (DyeColor dyeColor : DyeColor.values()) {
            TEXTURES.put(dyeColor, new ResourceLocation(Charm.MOD_ID, "textures/misc/" + dyeColor.getString() + "_glint.png"));

            GLINT.put(dyeColor, createGlint(dyeColor, TEXTURES.get(dyeColor)));
            ENTITY_GLINT.put(dyeColor, createEntityGlint(dyeColor, TEXTURES.get(dyeColor)));
            DIRECT_GLINT.put(dyeColor, createDirectGlint(dyeColor, TEXTURES.get(dyeColor)));
            DIRECT_ENTITY_GLINT.put(dyeColor, createDirectEntityGlint(dyeColor, TEXTURES.get(dyeColor)));
            ARMOR_GLINT.put(dyeColor, createArmorGlint(dyeColor, TEXTURES.get(dyeColor)));
            ARMOR_ENTITY_GLINT.put(dyeColor, createArmorEntityGlint(dyeColor, TEXTURES.get(dyeColor)));
        }

        hasInit = true;
    }

    public static DyeColor getDefaultClintColor() {
        return DyeColor.PURPLE;
    }

    public static RenderType createGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("glint_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getGlintTexturing())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static RenderType createEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("entity_glint_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getEntityGlintTexturing())
            .target(RenderStateAccessor.getItemTarget())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static RenderType createArmorGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("armor_glint_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getGlintTexturing())
            .layer(RenderStateAccessor.getViewOffsetZLayering())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static RenderType createArmorEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("armor_entity_glint_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getEntityGlintTexturing())
            .layer(RenderStateAccessor.getViewOffsetZLayering())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static RenderType createDirectGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("glint_direct_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getGlintTexturing())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static RenderType createDirectEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("entity_glint_direct_" + dyeColor.getString(), DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
            .texture(new RenderState.TextureState(texture, true, false))
            .writeMask(RenderStateAccessor.getColorWrite())
            .cull(RenderStateAccessor.getCullDisabled())
            .depthTest(RenderStateAccessor.getDepthEqual())
            .transparency(RenderStateAccessor.getGlintTransparency())
            .texturing(RenderStateAccessor.getEntityGlintTexturing())
            .target(RenderStateAccessor.getItemTarget())
            .build(false));

        getEntityBuilders().put(renderLayer, new BufferBuilder(renderLayer.getBufferSize()));
        return renderLayer;
    }

    public static IVertexBuilder getDirectItemGlintConsumer(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint, @Nullable ItemStack stack) {
        RenderType renderDirectGlint = RenderType.getGlintDirect();
        RenderType renderDirectEntityGlint = RenderType.getEntityGlintDirect();

        if (changeDefaultGlintColor) {
            renderDirectGlint = DIRECT_GLINT.get(getDefaultClintColor());
            renderDirectEntityGlint = DIRECT_ENTITY_GLINT.get(getDefaultClintColor());
        }

        if (stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(GLINT_TAG)) {
                    DyeColor dyeColor = DyeColor.valueOf(tag.getString(GLINT_TAG));
                    renderDirectGlint = DIRECT_GLINT.get(dyeColor);
                    renderDirectEntityGlint = DIRECT_ENTITY_GLINT.get(dyeColor);
                }
            }
        }

        return glint ? VertexBuilderUtils.newDelegate(provider.getBuffer(solid ? renderDirectGlint : renderDirectEntityGlint), provider.getBuffer(layer)) : provider.getBuffer(layer);
    }

    public static IVertexBuilder getItemGlintConsumer(IRenderTypeBuffer vertexConsumers, RenderType layer, boolean solid, boolean glint, @Nullable ItemStack stack) {
        if (glint) {
            RenderType renderGlint = RenderType.getGlint();
            RenderType renderEntityGlint = RenderType.getEntityGlint();

            if (changeDefaultGlintColor) {
                renderGlint = GLINT.get(getDefaultClintColor());
                renderEntityGlint = ENTITY_GLINT.get(getDefaultClintColor());
            }

            if (stack != null && stack.hasTag()) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    if (tag.contains(GLINT_TAG)) {
                        DyeColor dyeColor = DyeColor.byTranslationKey(tag.getString(GLINT_TAG), DyeColor.PURPLE);
                        renderGlint = GLINT.get(dyeColor);
                        renderEntityGlint = ENTITY_GLINT.get(dyeColor);
                    }
                }
            }

            return Minecraft.isFabulousGraphicsEnabled() && layer == Atlases.getItemEntityTranslucentCullType() ? VertexBuilderUtils.newDelegate(vertexConsumers.getBuffer(RenderType.getGlintTranslucent()), vertexConsumers.getBuffer(layer)) : VertexBuilderUtils.newDelegate(vertexConsumers.getBuffer(solid ? renderGlint : renderEntityGlint), vertexConsumers.getBuffer(layer));
        } else {
            return vertexConsumers.getBuffer(layer);
        }
    }

    public static IVertexBuilder getArmorGlintConsumer(IRenderTypeBuffer provider, RenderType layer, boolean solid, boolean glint, @Nullable ItemStack stack) {
        if (glint) {
            RenderType renderArmorGlint = RenderType.getArmorGlint();
            RenderType renderArmorEntityGlint = RenderType.getArmorEntityGlint();

            if (changeDefaultGlintColor) {
                renderArmorGlint = ARMOR_GLINT.get(getDefaultClintColor());
                renderArmorEntityGlint = ARMOR_ENTITY_GLINT.get(getDefaultClintColor());
            }

            if (stack != null && stack.hasTag()) {
                CompoundNBT tag = stack.getTag();
                if (tag != null) {
                    if (tag.contains(GLINT_TAG)) {
                        DyeColor dyeColor = DyeColor.byTranslationKey(tag.getString(GLINT_TAG), DyeColor.PURPLE);
                        renderArmorGlint = ARMOR_GLINT.get(dyeColor);
                        renderArmorEntityGlint = ARMOR_ENTITY_GLINT.get(dyeColor);
                    }
                }
            }

            return VertexBuilderUtils.newDelegate(provider.getBuffer(solid ? renderArmorGlint : renderArmorEntityGlint), provider.getBuffer(layer));
        } else {
            return provider.getBuffer(layer);
        }
    }

    private static SortedMap<RenderType, BufferBuilder> getEntityBuilders() {
        RenderTypeBuffers bufferBuilders = ((MinecraftAccessor) Minecraft.getInstance()).getRenderTypeBuffers();
        return ((RenderTypeBuffersMixin)bufferBuilders).getFixedBuffers();
    }
}

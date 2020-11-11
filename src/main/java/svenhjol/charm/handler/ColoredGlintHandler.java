package svenhjol.charm.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.Charm;
import svenhjol.charm.mixin.accessor.MinecraftAccessor;
import svenhjol.charm.mixin.accessor.RenderStateAccessor;
import svenhjol.charm.mixin.accessor.RenderTypeBuffersMixin;

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

    public static boolean isEnabled;
    public static ItemStack targetStack;

    private static boolean hasInit = false;

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
    
    public static DyeColor getDefaultGlintColor() {
        // TODO: should be configurable, maybe in core
        return DyeColor.PURPLE;
    }

    public static DyeColor getStackColor(ItemStack stack) {
        if (stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(GLINT_TAG))
                    return DyeColor.byTranslationKey(tag.getString(GLINT_TAG), DyeColor.PURPLE);
            }
        }

        return getDefaultGlintColor();
    }

    public static RenderType getArmorGlintRenderLayer() {
        return ARMOR_GLINT.get(getStackColor(targetStack));
    }

    public static RenderType getArmorEntityGlintRenderLayer() {
        return ARMOR_ENTITY_GLINT.get(getStackColor(targetStack));
    }

    public static RenderType getDirectGlintRenderLayer() {
        return DIRECT_GLINT.get(getStackColor(targetStack));
    }

    public static RenderType getDirectEntityGlintRenderLayer() {
        return DIRECT_ENTITY_GLINT.get(getStackColor(targetStack));
    }

    public static RenderType getEntityGlintRenderLayer() {
        return ENTITY_GLINT.get(getStackColor(targetStack));
    }

    public static RenderType getGlintRenderLayer() {
        return GLINT.get(getStackColor(targetStack));
    }

    private static RenderType createGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static RenderType createEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static RenderType createArmorGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static RenderType createArmorEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static RenderType createDirectGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static RenderType createDirectEntityGlint(DyeColor dyeColor, ResourceLocation texture) {
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

    private static SortedMap<RenderType, BufferBuilder> getEntityBuilders() {
        RenderTypeBuffers bufferBuilders = ((MinecraftAccessor) Minecraft.getInstance()).getRenderTypeBuffers();
        return ((RenderTypeBuffersMixin)bufferBuilders).getFixedBuffers();
    }
}

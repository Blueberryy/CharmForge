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
import svenhjol.charm.mixin.accessor.RenderTypeAccessor;
import svenhjol.charm.mixin.accessor.RenderTypeBuffersMixin;
import svenhjol.charm.module.Core;

import java.util.*;
import java.util.stream.Collectors;

public class ColoredGlintHandler {
    public static final String GLINT_TAG = "charm_glint";

    public static Map<String, ResourceLocation> TEXTURES = new HashMap<>();
    public static Map<String, RenderType> GLINT = new HashMap<>();
    public static Map<String, RenderType> ENTITY_GLINT = new HashMap<>();
    public static Map<String, RenderType> DIRECT_GLINT = new HashMap<>();
    public static Map<String, RenderType> DIRECT_ENTITY_GLINT = new HashMap<>();
    public static Map<String, RenderType> ARMOR_GLINT = new HashMap<>();
    public static Map<String, RenderType> ARMOR_ENTITY_GLINT = new HashMap<>();

    public static String defaultGlintColor;
    public static ItemStack targetStack;

    private static boolean hasInit = false;

    public static void init() {
        if (hasInit)
            return;

        for (DyeColor dyeColor : DyeColor.values()) {
            String color = dyeColor.getString();
            TEXTURES.put(color, new ResourceLocation(Charm.MOD_ID, "textures/misc/" + color + "_glint.png"));

            GLINT.put(color, createGlint(color, TEXTURES.get(color)));
            ENTITY_GLINT.put(color, createEntityGlint(color, TEXTURES.get(color)));
            DIRECT_GLINT.put(color, createDirectGlint(color, TEXTURES.get(color)));
            DIRECT_ENTITY_GLINT.put(color, createDirectEntityGlint(color, TEXTURES.get(color)));
            ARMOR_GLINT.put(color, createArmorGlint(color, TEXTURES.get(color)));
            ARMOR_ENTITY_GLINT.put(color, createArmorEntityGlint(color, TEXTURES.get(color)));
        }

        // check that the configured glint color is valid
        List<String> validColors = Arrays.stream(DyeColor.values()).map(DyeColor::getString).collect(Collectors.toList());
        validColors.add("rainbow");

        defaultGlintColor = validColors.contains(Core.glintColor) ? Core.glintColor : DyeColor.PURPLE.getString();

        hasInit = true;
    }
    
    public static String getDefaultGlintColor() {
        return defaultGlintColor;
    }

    public static String getStackColor(ItemStack stack) {
        if (stack != null && stack.hasTag()) {
            CompoundNBT tag = stack.getTag();
            if (tag != null) {
                if (tag.contains(GLINT_TAG))
                    return tag.getString(GLINT_TAG);
            }
        }

        return getDefaultGlintColor();
    }

    public static RenderType getArmorGlintRenderLayer() {
        return ARMOR_GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getArmorGlint());
    }

    public static RenderType getArmorEntityGlintRenderLayer() {
        return ARMOR_ENTITY_GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getArmorEntityGlint());
    }

    public static RenderType getDirectGlintRenderLayer() {
        return DIRECT_GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getDirectGlint());
    }

    public static RenderType getDirectEntityGlintRenderLayer() {
        return DIRECT_ENTITY_GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getDirectEntityGlint());
    }

    public static RenderType getEntityGlintRenderLayer() {
        return ENTITY_GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getEntityGlint());
    }

    public static RenderType getGlintRenderLayer() {
        return GLINT.getOrDefault(getStackColor(targetStack), RenderTypeAccessor.getGlint());
    }

    private static RenderType createGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("glint_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

    private static RenderType createEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("entity_glint_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

    private static RenderType createArmorGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("armor_glint_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

    private static RenderType createArmorEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("armor_entity_glint_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

    private static RenderType createDirectGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("glint_direct_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

    private static RenderType createDirectEntityGlint(String color, ResourceLocation texture) {
        RenderType renderLayer = RenderType.makeType("entity_glint_direct_" + color, DefaultVertexFormats.POSITION_TEX, 7, 256, RenderType.State.getBuilder()
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

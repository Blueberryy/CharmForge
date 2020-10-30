package svenhjol.charm.mixin.accessor;

import net.minecraft.client.renderer.RenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderState.class)
public interface RenderStateAccessor {
    @Accessor("COLOR_MASK")
    static RenderState.WriteMaskState getColorMask() {
        throw new IllegalStateException();
    }

    @Accessor("DISABLE_CULLING")
    static RenderState.Cull getDisableCulling() {
        throw new IllegalStateException();
    }

    @Accessor("EQUAL_DEPTH_TEST")
    static RenderState.DepthTest getEqualDepthTest() {
        throw new IllegalStateException();
    }

    @Accessor("GLINT_TRANSPARENCY")
    static RenderState.Transparency getGlintTransparency() {
        throw new IllegalStateException();
    }

    @Accessor("GLINT_TEXTURING")
    static RenderState.Texturing getGlintTexturing() {
        throw new IllegalStateException();
    }

    @Accessor("ENTITY_GLINT_TEXTURING")
    static RenderState.Texturing getEntityGlintTexturing() {
        throw new IllegalStateException();
    }

    @Accessor("ITEM_TARGET")
    static RenderState.Target getItemTarget() {
        throw new IllegalStateException();
    }

    @Accessor("VIEW_OFFSET_Z_LAYERING")
    static RenderState.Layering getViewOffsetZLayering() {
        throw new IllegalStateException();
    }
}

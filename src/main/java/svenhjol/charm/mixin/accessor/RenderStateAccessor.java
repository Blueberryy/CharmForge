package svenhjol.charm.mixin.accessor;

import net.minecraft.client.renderer.RenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderState.class)
public interface RenderStateAccessor {
    @Accessor("COLOR_WRITE")
    static RenderState.WriteMaskState getColorWrite() {
        throw new IllegalStateException();
    }

    @Accessor("CULL_DISABLED")
    static RenderState.CullState getCullDisabled() {
        throw new IllegalStateException();
    }

    @Accessor("DEPTH_EQUAL")
    static RenderState.DepthTestState getDepthEqual() {
        throw new IllegalStateException();
    }

    @Accessor("GLINT_TRANSPARENCY")
    static RenderState.TransparencyState getGlintTransparency() {
        throw new IllegalStateException();
    }

    @Accessor("GLINT_TEXTURING")
    static RenderState.TexturingState getGlintTexturing() {
        throw new IllegalStateException();
    }

    @Accessor("ENTITY_GLINT_TEXTURING")
    static RenderState.TexturingState getEntityGlintTexturing() {
        throw new IllegalStateException();
    }

    @Accessor("field_241712_U_")
    static RenderState.TargetState getItemTarget() {
        throw new IllegalStateException();
    }

    @Accessor("field_239235_M_")
    static RenderState.LayerState getViewOffsetZLayering() {
        throw new IllegalStateException();
    }
}

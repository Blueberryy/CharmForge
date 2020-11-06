package svenhjol.charm.render;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.module.VariantMobTextures;

public class VariantMobRenderer {
    public static class Chicken extends ChickenRenderer {
        public Chicken(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(ChickenEntity entity) {
            return VariantMobTextures.getChickenTexture(entity);
        }
    }

    public static class Cow extends CowRenderer {
        public Cow(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(CowEntity entity) {
            return VariantMobTextures.getCowTexture(entity);
        }
    }

    public static class Pig extends PigRenderer {
        public Pig(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(PigEntity entity) {
            return VariantMobTextures.getPigTexture(entity);
        }
    }

    public static class Sheep extends SheepRenderer {
        public Sheep(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SheepEntity entity) {
            return VariantMobTextures.getSheepTexture(entity);
        }
    }

    public static class SnowGolem extends SnowManRenderer {
        public SnowGolem(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SnowGolemEntity entity) {
            return VariantMobTextures.getSnowGolemTexture(entity);
        }
    }

    public static class Squid extends SquidRenderer {
        public Squid(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SquidEntity entity) {
            return VariantMobTextures.getSquidTexture(entity);
        }
    }

    public static class Wolf extends WolfRenderer {
        public Wolf(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(WolfEntity entity) {
            return VariantMobTextures.getWolfTexture(entity);
        }
    }
}

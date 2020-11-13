package svenhjol.charm.render;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.client.VariantMobTexturesClient;

public class VariantMobRenderer {
    public static class Chicken extends ChickenRenderer {
        public Chicken(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(ChickenEntity entity) {
            return VariantMobTexturesClient.getChickenTexture(entity);
        }
    }

    public static class Cow extends CowRenderer {
        public Cow(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(CowEntity entity) {
            return VariantMobTexturesClient.getCowTexture(entity);
        }
    }

    public static class Pig extends PigRenderer {
        public Pig(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(PigEntity entity) {
            return VariantMobTexturesClient.getPigTexture(entity);
        }
    }

    public static class Sheep extends SheepRenderer {
        public Sheep(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SheepEntity entity) {
            return VariantMobTexturesClient.getSheepTexture(entity);
        }
    }

    public static class SnowGolem extends SnowManRenderer {
        public SnowGolem(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SnowGolemEntity entity) {
            return VariantMobTexturesClient.getSnowGolemTexture(entity);
        }
    }

    public static class Squid extends SquidRenderer {
        public Squid(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(SquidEntity entity) {
            return VariantMobTexturesClient.getSquidTexture(entity);
        }
    }

    public static class Wolf extends WolfRenderer {
        public Wolf(EntityRendererManager dispatcher) {
            super(dispatcher);
        }

        @Override
        public ResourceLocation getEntityTexture(WolfEntity entity) {
            return VariantMobTexturesClient.getWolfTexture(entity);
        }
    }
}

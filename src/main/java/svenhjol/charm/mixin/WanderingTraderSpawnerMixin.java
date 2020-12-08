package svenhjol.charm.mixin;

import net.minecraft.world.spawner.WanderingTraderSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import svenhjol.charm.module.WanderingTraderImprovements;

import java.util.Random;

@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderSpawnerMixin {
    @Redirect(
        method = "func_234562_a_",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextInt(I)I"
        )
    )
    private int hookRandomCheck(Random random, int i) {
        return WanderingTraderImprovements.shouldSpawnFrequently() ? 0 : 10; // 10 is vanilla random value
    }
}

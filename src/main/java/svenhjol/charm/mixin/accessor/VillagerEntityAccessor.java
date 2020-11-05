package svenhjol.charm.mixin.accessor;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerEntity.class)
public interface VillagerEntityAccessor {
    @Invoker
    void invokeShakeHead();

    @Invoker
    boolean invokeCanLevelUp();

    @Invoker
    void invokeLevelUp();
}
